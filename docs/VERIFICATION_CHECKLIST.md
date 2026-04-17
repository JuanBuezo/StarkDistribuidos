# ✅ CHECKLIST DE VERIFICACIÓN

## 📋 Pre-Inicio

- [ ] Los 8 archivos `application.yaml` tienen credenciales de Eureka
- [ ] Eureka Server está en puerto 8761
- [ ] No hay otros procesos usando los puertos 8080-8085, 8761
- [ ] Maven está instalado (mvnw.cmd disponible)
- [ ] Java 17+ está instalado

## 🚀 Orden de Inicio (CRÍTICO)

- [ ] **1. Eureka Server** (starkDistribuidos-config)
  - [ ] Comando: `..\mvnw.cmd spring-boot:run`
  - [ ] Esperado: "Started ConfigServerApplication"
  - [ ] Verificar: http://localhost:8761 accesible
  - [ ] Tiempo esperado: 10-15 segundos

- [ ] **2. API Gateway** (starkDistribuidos-gateway)
  - [ ] Comando: `..\mvnw.cmd spring-boot:run`
  - [ ] Esperado: "Started GatewayApplication"
  - [ ] Verificar: Aparece en Eureka
  - [ ] Tiempo esperado: 8-10 segundos después de Eureka

- [ ] **3. Access Service** (starkDistribuidos-access)
  - [ ] Comando: `..\mvnw.cmd spring-boot:run`
  - [ ] Esperado: "Started AccessServiceApplication"
  - [ ] Verificar: Aparece en Eureka
  - [ ] Tiempo esperado: 6-8 segundos

- [ ] **4. Otros servicios** (en este orden):
  - [ ] Sensor Service (8082)
  - [ ] Alert Service (8083)
  - [ ] Auth Service (8081)
  - [ ] Notification Service (8085)
  - [ ] Frontend (si es necesario)

## ✅ Verificación de Eureka

Después de iniciar todos los servicios (espera 2-3 minutos):

Accede a: http://localhost:8761

- [ ] **General Info:**
  - [ ] `Lease expiration enabled`: true
  - [ ] `Renews threshold`: 1
  - [ ] `Renews (last min)`: 0 → aumentando con el tiempo

- [ ] **DS Replicas:**
  - [ ] Solo muestra "No instances available" (normal, sin cluster)

- [ ] **Instances currently registered:**
  - [ ] [ ] STARK-GATEWAY (1 instance, UP)
  - [ ] STARK-ACCESS (1 instance, UP)
  - [ ] STARK-SENSOR (1 instance, UP)
  - [ ] STARK-ALERT (1 instance, UP)
  - [ ] STARK-AUTH (1 instance, UP)
  - [ ] STARK-NOTIFICATION (1 instance, UP)

- [ ] **Estado de las instancias:**
  - [ ] Color: 🟢 Verde (UP)
  - [ ] Status: "UP" en la columna Status
  - [ ] Tipo de instancia: "Standalone" (cada uno)

## 🔍 Verificación de Logs (en cada terminal)

### Terminal Eureka
- [ ] ✅ No hay errores de Spring Security
- [ ] ✅ Los clientes se registran (búsqueda por "registering service")
- [ ] ✅ Heartbeats recibidos regularmente

### Terminal Gateway
- [ ] ✅ No hay errores 401 de Eureka
- [ ] ✅ "Registering application STARK-GATEWAY with eureka"
- [ ] ✅ Escucha en puerto 8080

### Terminal Access Service
- [ ] ✅ No hay errores 401 de Eureka
- [ ] ✅ "Registering application STARK-ACCESS with eureka"
- [ ] ✅ Escucha en puerto 8084

### Otros servicios
- [ ] ✅ Similares a los anteriores (sin errores 401)
- [ ] ✅ Se registran correctamente

## 🚫 Errores que NO deben aparecer

❌ **Evitar estos mensajes:**
```
Request execution failure with status code 401
Cannot execute request on any known server
Unable to refresh its cache
Unable to send heartbeat
Registration failed
```

Si ves estos, significa que algo no está configurado correctamente.

## 🧪 Pruebas Funcionales

### Test 1: Eureka REST API
```bash
curl -u admin:admin123 http://localhost:8761/eureka/apps/
# Esperado: XML/JSON con lista de aplicaciones
```

### Test 2: Gateway está disponible
```bash
curl http://localhost:8080/
# Esperado: Respuesta del gateway (puede ser error si no hay ruta)
```

### Test 3: Descubrimiento de servicios
```bash
curl -u admin:admin123 http://localhost:8761/eureka/apps/STARK-ACCESS/
# Esperado: Información de STARK-ACCESS con instancias
```

### Test 4: Health check de Gateway
```bash
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}
```

## 📊 Monitoreo Continuo

**Comandos útiles mientras está corriendo:**

### Ver instancias registradas en tiempo real
```powershell
# En PowerShell, abre URL de Eureka
Start-Process "http://localhost:8761"
```

### Verificar logs en tiempo real (en cada terminal)
```
Watch for messages containing:
- "Registering application"
- "InstanceInfoReplicator" (sin WARN)
- "Tomcat started on port"
```

### Monitorizar puertos activos
```powershell
netstat -ano | findstr :8080
netstat -ano | findstr :8761
netstat -ano | findstr :8084
# etc.
```

## 🆘 Troubleshooting

### Síntoma: Error 401 en logs
```
✅ Solución: Verifica credenciales en application.yaml
admin:admin123 debe estar presente en la URL
```

### Síntoma: Puerto ya está en uso
```
✅ Solución: 
netstat -ano | findstr :PUERTO
taskkill /PID 12345 /F
```

### Síntoma: Servicio no aparece en Eureka después de 2 min
```
✅ Solución:
1. Verifica que el puerto sea único
2. Verifica que Eureka esté en 8761
3. Reinicia el servicio
4. Mira los logs buscando errores
```

### Síntoma: "Connection refused"
```
✅ Solución:
1. Eureka no está corriendo
2. Verifica: http://localhost:8761
3. Inicia Eureka primero
4. Espera 15 segundos
```

## 📈 Performance

**Tiempos esperados:**
- Eureka Server init: 10-15 seg
- Por cada servicio: 6-10 seg
- Tiempo total para 7 servicios: ~2-3 minutos

**Señales de salud:**
- ✅ Todos los servicios en estado "UP"
- ✅ Logs sin WARN ni ERROR sobre Eureka
- ✅ Renews count aumentando gradualmente
- ✅ Cada servicio responde a health checks

## 📋 Configuración Validada

Los siguientes archivos han sido actualizados y verificados:

```
✅ starkDistribuidos-access/src/main/resources/application.yaml
✅ starkDistribuidos-auth/src/main/resources/application.yaml
✅ starkDistribuidos-alert/src/main/resources/application.yaml
✅ starkDistribuidos-gateway/src/main/resources/application.yaml
✅ starkDistribuidos-notification/src/main/resources/application.yaml
✅ starkDistribuidos-sensor/src/main/resources/application.yaml
✅ starkDistribuidos-frontend/src/main/resources/application.yaml
```

## 🎉 Criterios de Éxito

Cuando todo esté correcto, deberías ver:

1. ✅ Eureka en http://localhost:8761 mostrando 7 servicios
2. ✅ Todos los servicios con status "UP"
3. ✅ Sin errores 401 en ningún log
4. ✅ Cada servicio responde en su puerto
5. ✅ Gateway puede enrutar a través de nombres de servicio
6. ✅ Heartbeats se renuevan cada 30 segundos

---

**Usa este checklist para validar que todo funciona correctamente después de iniciar el sistema.**

Si algún item está marcado como ❌, revisa la sección de Troubleshooting.

