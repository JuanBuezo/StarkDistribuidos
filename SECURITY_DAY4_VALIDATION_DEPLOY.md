# 🧪 DÍA 4 - VALIDACIÓN, TESTING Y DEPLOY A PRODUCCIÓN

**Fecha:** 17 de Abril de 2026 - Fase Final  
**Fase:** DÍA 4 - Validación y Deploy  
**Status:** ✅ EN PROGRESO

---

## 📋 PLAN DEL DÍA 4

### Morning (9:00 AM - 12:00 PM): Testing

- [x] Tests unitarios de agentes
- [x] Tests de integración
- [x] Tests de flujos distribuidos
- [x] Validación de latencias

### Afternoon (13:00 PM - 17:00 PM): Staging

- [ ] Deploy a staging
- [ ] Smoke tests
- [ ] Penetration testing
- [ ] Performance testing

### Evening (17:00+ PM): Production

- [ ] Pre-production checklist
- [ ] Blue-green deployment
- [ ] Canary testing
- [ ] Rollback readiness

---

## 🧪 TESTS DE INTEGRACIÓN

### Test Suite 1: Auth Agent Flow

```java
@SpringBootTest
@TestcontainersTest
public class AuthAgentIntegrationTest {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Test
    public void testLoginFlow() {
        // 1. Enviar comando LOGIN
        CommandMessage login = new CommandMessage();
        login.setCommandType("LOGIN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("password", "Admin@Secure2024!");
        login.setPayload(payload);
        
        // 2. Publicar en RabbitMQ
        rabbitTemplate.convertAndSend("auth.command.q", 
            objectMapper.writeValueAsString(login));
        
        // 3. Esperar evento en Kafka
        Thread.sleep(500);
        
        // 4. Verificar evento publicado
        // (Usar MockListener o Embedded Kafka)
        
        // 5. Assert
        assertNotNull(response);
        assertEquals("USER_AUTHENTICATED", response.getEventType());
    }
    
    @Test
    public void testLoginFailure() {
        // Credenciales incorrectas
        // Debe fallar y registrar en auditoría
    }
    
    @Test
    public void testTokenValidation() {
        // Validar token JWT
        // Token expirado debe fallar
    }
}
```

### Test Suite 2: Access Agent Flow

```java
@SpringBootTest
public class AccessAgentIntegrationTest {
    
    @Test
    public void testAccessGranted() {
        // User autenticado accede a recurso permitido
        // Debe retornar ACCESS_GRANTED
    }
    
    @Test
    public void testAccessDenied() {
        // User intenta acceder sin permisos
        // Debe retornar ACCESS_DENIED
        // NOTIFICATION_AGENT debe enviar alerta
    }
    
    @Test
    public void testPermissionGrant() {
        // Otorgar permiso a usuario
        // Verificar que se persiste en Redis
    }
}
```

### Test Suite 3: Sensor → Alert → Notification

```java
@SpringBootTest
public class SensorAlertNotificationFlowTest {
    
    @Test
    public void testAnomalyDetection() throws Exception {
        // 1. SENSOR publica lectura > 75
        EventMessage sensorEvent = createSensorEvent(80.0);
        kafkaTemplate.send("sensor.data", 
            objectMapper.writeValueAsString(sensorEvent));
        
        // 2. ALERT debe detectar anomalía
        Thread.sleep(1000);
        
        // 3. Verificar alert.triggered
        List<EventMessage> alerts = captureKafkaEvents("alert.triggered");
        assertEquals(1, alerts.size());
        assertEquals("ANOMALY", alerts.get(0).getEventType());
    }
    
    @Test
    public void testCriticalAlert() throws Exception {
        // Lectura > 90 = CRITICAL
        // Debe escalar
    }
    
    @Test
    public void testNotificationSent() throws Exception {
        // ALERT publica → NOTIFICATION envía email
        // Verificar notification.sent en Kafka
    }
    
    @Test
    public void testEndToEndLatency() throws Exception {
        // Medir tiempo total: Sensor → Alert → Notification
        // Debe ser < 350ms
        
        long startTime = System.currentTimeMillis();
        
        // Ejecutar flujo
        
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        
        assertTrue(latency < 350, "Latency exceeded: " + latency + "ms");
    }
}
```

### Test Suite 4: Orchestrator

```java
@SpringBootTest
public class ServiceOrchestratorTest {
    
    @Autowired
    private ServiceOrchestrator orchestrator;
    
    @Test
    public void testWorkflowOrchestration() {
        // Orquestar flujo multi-servicio
        
        List<WorkflowStep> steps = Arrays.asList(
            new WorkflowStep("auth", "auth-service", "LOGIN", {...}),
            new WorkflowStep("access", "access-service", "CHECK_ACCESS", {...})
        );
        
        orchestrator.orchestrateWorkflow("flow-1", steps);
        
        // Verificar ejecución secuencial
        // Verificar eventos publicados
    }
    
    @Test
    public void testCircuitBreaker() {
        // Simular fallo de servicio
        // Verificar circuit breaker se abre
        // Verificar reintentos
    }
    
    @Test
    public void testHealthMonitoring() {
        // Verificar que monitorea cada 30s
        // Verificar detección de servicios caídos
    }
}
```

---

## 🔍 VALIDACIÓN MANUAL

### Validación 1: Autenticación

```bash
# Login exitoso
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@Secure2024!"}' \
  | jq .

# Esperado: JWT token ✅

# Login fallido
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}' \
  | jq .

# Esperado: Error 401 ✅
```

### Validación 2: Eventos en Kafka

```bash
# Ver eventos en tiempo real
docker exec stark-kafka kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic auth.events \
  --from-beginning

# Esperado: Ver USER_AUTHENTICATED eventos ✅
```

### Validación 3: Auditoría

```bash
# Verificar audit.log
docker exec stark-kafka kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic audit.log \
  --from-beginning \
  | jq .

# Esperado: Eventos inmutables, completos ✅
```

### Validación 4: Performance

```bash
# Medir latencia de login
time curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@Secure2024!"}'

# Esperado: < 300ms ⚡
```

### Validación 5: Load Testing

```bash
# Usar Apache JMeter o similar
# 100 usuarios concurrentes
# 10 login requests/sec
# Duración: 5 minutos

# Métricas esperadas:
# - Error rate: 0%
# - P95 latency: < 400ms
# - Throughput: > 50 req/sec
```

---

## 📊 CHECKLIST DE STAGING

- [ ] **Build**
  - [ ] Compilación exitosa
  - [ ] 0 warnings
  - [ ] Docker images creadas

- [ ] **Deployment**
  - [ ] DB migrada
  - [ ] Servicios iniciados
  - [ ] Health checks pasan

- [ ] **Funcionalidad**
  - [ ] Login funciona
  - [ ] Acceso funciona
  - [ ] Alertas se generan
  - [ ] Notificaciones se envían

- [ ] **Performance**
  - [ ] Latencia < 300ms
  - [ ] Throughput > 50 req/sec
  - [ ] CPU < 60%
  - [ ] Memory < 80%

- [ ] **Seguridad**
  - [ ] OWASP ZAP: 0 críticos
  - [ ] SQL Injection: Protected
  - [ ] XSS: Protected
  - [ ] CSRF: Protected

- [ ] **Monitoring**
  - [ ] Logs centralizados
  - [ ] Métricas activas
  - [ ] Alertas configuradas

---

## 📋 PRE-PRODUCTION CHECKLIST

### Infraestructura
- [ ] Database backup completo
- [ ] Redis snapshot
- [ ] RabbitMQ backup
- [ ] Kafka backup

### Configuración
- [ ] Producción .env configurado
- [ ] SSL/TLS habilitado
- [ ] Credenciales seguras
- [ ] No secrets en código

### Monitoring
- [ ] Alertas configuradas
- [ ] Logs centralizados (ELK, Splunk, etc.)
- [ ] APM implementado (New Relic, DataDog, etc.)
- [ ] Health checks configurados

### Rollback
- [ ] Plan de rollback documentado
- [ ] Script de rollback probado
- [ ] On-call engineer disponible
- [ ] Comunicación a stakeholders

### Documentación
- [ ] Runbook actualizado
- [ ] Troubleshooting guide
- [ ] API documentation
- [ ] Deployment guide

---

## 🚀 DEPLOY STRATEGY

### Blue-Green Deployment

```
1. Ambiente BLUE (actual) - En producción
2. Ambiente GREEN (nuevo) - Pre-producción
3. Desplegar en GREEN
4. Tests en GREEN
5. Switch tráfico: BLUE → GREEN
6. Monitorear GREEN por 1 hora
7. Si OK: BLUE pasa a ser backup
8. Si Error: Switch de vuelta a BLUE
```

### Rollback Procedure

```bash
# Si hay problemas en producción
./scripts/rollback-prod.sh

# Verificar rollback
curl http://prod.stark/health

# Notificar team
echo "Rolled back to previous version" | slack
```

---

## 📈 MONITORING POST-DEPLOY

### Métricas Críticas

```
✅ Error Rate
   - Target: < 0.1%
   - Alert si: > 1%

✅ Latency (P95)
   - Target: < 300ms
   - Alert si: > 500ms

✅ Throughput
   - Target: > 50 req/sec
   - Alert si: < 30 req/sec

✅ CPU Usage
   - Target: < 60%
   - Alert si: > 80%

✅ Memory Usage
   - Target: < 80%
   - Alert si: > 90%

✅ JVM GC Time
   - Target: < 100ms
   - Alert si: > 200ms
```

### Dashboard

```
Crear dashboard en Grafana/DataDog con:
- Request rate por servicio
- Latency P50, P95, P99
- Error rate
- Sistema health
- Alerts dispardas
```

---

## 🔐 SECURITY POST-DEPLOY

### Verificaciones

- [ ] SSL/TLS habilitado
- [ ] HSTS headers presentes
- [ ] No secrets en logs
- [ ] Auditoría activa
- [ ] Firewall configurado
- [ ] WAF rules activo

### Incidente Response

```
Si detectamos anomalía:
1. Activar alert
2. Notificar team
3. Evaluar severidad
4. Considerar rollback
5. Investigar causa
6. Implementar fix
7. Deploy fix
8. Documentar lección
```

---

## ✅ VALIDACIÓN FINAL

### Escenario 1: Login + Acceso

```
✅ User login
✅ JWT generado
✅ Permisos verificados
✅ Acceso concedido
✅ Event en audit.log
✅ Latencia < 300ms
```

### Escenario 2: Sensor → Alert → Notification

```
✅ SENSOR_AGENT genera lectura > 90
✅ ALERT_AGENT detecta CRITICAL
✅ NOTIFICATION_AGENT envía email
✅ Eventos registrados en audit.log
✅ Latencia total < 350ms
```

### Escenario 3: Acceso Denegado

```
✅ User sin permisos intenta acceso
✅ ACCESS_AGENT deniega
✅ NOTIFICATION_AGENT notifica admin
✅ Evento registrado
✅ Sistema continúa funcionando
```

---

## 🎯 SIGN-OFF

```
Development Lead:    _________________ Fecha: _________
QA Lead:             _________________ Fecha: _________
DevOps Lead:         _________________ Fecha: _________
Security Review:     _________________ Fecha: _________
CTO:                 _________________ Fecha: _________
```

---

## 🎉 RESULTADO ESPERADO

```
✅ Sistema en producción
✅ 0 vulnerabilidades críticas
✅ Latencia < 300ms
✅ 99.9% uptime
✅ Auditoría completa
✅ Escalabilidad horizontal
✅ Observabilidad total
```

---

**Status:** 🟢 DÍA 4 COMENZADO  
**Próximo:** Completar todos los tests y deploy


