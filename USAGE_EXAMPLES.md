# 💡 EJEMPLOS DE USO - CASOS PRÁCTICOS

**Fecha:** 17 de Abril de 2026  
**Proyecto:** STARK INDUSTRIES  
**Propósito:** Guía de uso real del sistema

---

## 🚀 INICIO RÁPIDO

### Paso 1: Compilar el Proyecto
```bash
# En la raíz del proyecto
mvn clean package -DskipTests

# Verificar builds
mvn package -T 1C
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2 min 45 sec
```

---

### Paso 2: Iniciar la Infraestructura
```bash
# Iniciar Docker Compose
docker-compose -f docker-compose-sdd.yml up -d

# Verificar que todo está corriendo
docker-compose -f docker-compose-sdd.yml ps

# Resultado:
# NAME                    STATUS
# postgres               Up 30s
# redis                  Up 25s
# rabbitmq               Up 20s
# zookeeper              Up 15s
# kafka                  Up 12s
```

---

### Paso 3: Verificar Servicios
```bash
# Health check del gateway
curl http://localhost:8080/actuator/health

# Respuesta:
# {
#   "status": "UP"
# }

# Health check de Eureka
curl http://localhost:8761/actuator/health

# Respuesta:
# {
#   "status": "UP"
# }
```

---

## 🔐 EJEMPLO 1: AUTENTICACIÓN

### Caso: Usuario Login

**Request:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -H "X-Requested-With: XMLHttpRequest" \
  -d '{
    "username": "admin",
    "password": "Admin@Secure2024!"
  }' \
  -w "\nLatencia: %{time_total}s\n"
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMzM4Nzg0NSwiZXhwIjoxNzEzNDc0MjQ1fQ.x1Kz9...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@stark.com",
    "role": "ADMIN"
  },
  "expiresIn": 86400,
  "refreshToken": "refresh_token_xyz..."
}
```

**Latencia:** 285ms ✅

**Logs (Backend):**
```
[2026-04-17 14:30:45] INFO  AuthAgent - Usuario 'admin' se autentico exitosamente
[2026-04-17 14:30:45] INFO  JWT - Token generado con expiracion: 2026-04-18 14:30:45
[2026-04-17 14:30:46] INFO  Audit - LOGIN_SUCCESS | usuario=admin | ip=127.0.0.1 | timestamp=2026-04-17T14:30:45Z
```

---

### Caso: Login Fallido

**Request:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "WrongPassword"
  }'
```

**Response (401 Unauthorized):**
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Credenciales inválidas",
  "timestamp": "2026-04-17T14:30:50Z",
  "requestId": "req-123456"
}
```

**Audit:**
```
[2026-04-17 14:30:50] WARN  AuthAgent - Intento fallido de login: usuario=admin
[2026-04-17 14:30:50] WARN  Audit - LOGIN_FAILED | usuario=admin | razon=INVALID_CREDENTIALS | ip=127.0.0.1
```

---

### Caso: Token Expirado

**Request:**
```bash
# Usar token expirado
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcxMzI5NjQ0NX0.invalid..."

curl -X GET http://localhost:8080/api/sensors/readings \
  -H "Authorization: Bearer $TOKEN"
```

**Response (401 Unauthorized):**
```json
{
  "error": "TOKEN_EXPIRED",
  "message": "Token expirado. Debe refrescar o iniciar sesión nuevamente",
  "timestamp": "2026-04-17T14:35:10Z"
}
```

---

## 🔒 EJEMPLO 2: CONTROL DE ACCESO

### Caso: Admin accede a panel admin

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.valid_token"

curl -X POST http://localhost:8084/api/access/check \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "admin",
    "resource": "admin_panel",
    "action": "READ"
  }'
```

**Response (200 OK):**
```json
{
  "userId": "admin",
  "resource": "admin_panel",
  "action": "READ",
  "status": "GRANTED",
  "reason": "Usuario es ADMIN",
  "timestamp": "2026-04-17T14:35:15Z"
}
```

**Audit:**
```
[2026-04-17 14:35:15] INFO  AccessAgent - Permiso OTORGADO
  usuario=admin | recurso=admin_panel | accion=READ | razon=ROLE_ADMIN
```

---

### Caso: Usuario regular intenta acceder a panel admin

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTAxIn0.valid_token"

curl -X POST http://localhost:8084/api/access/check \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user101",
    "resource": "admin_panel",
    "action": "READ"
  }'
```

**Response (403 Forbidden):**
```json
{
  "userId": "user101",
  "resource": "admin_panel",
  "action": "READ",
  "status": "DENIED",
  "reason": "Rol VIEWER no tiene permiso para admin_panel",
  "timestamp": "2026-04-17T14:35:20Z",
  "auditId": "audit-789456"
}
```

**Audit:**
```
[2026-04-17 14:35:20] WARN  AccessAgent - Permiso DENEGADO
  usuario=user101 | recurso=admin_panel | accion=READ | razon=INSUFFICIENT_PERMISSIONS
  | auditId=audit-789456
```

---

### Caso: Listar permisos de usuario

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.token"

curl -X GET http://localhost:8084/api/access/permissions/admin \
  -H "Authorization: Bearer $TOKEN"
```

**Response (200 OK):**
```json
{
  "userId": "admin",
  "role": "ADMIN",
  "permissions": [
    {
      "resource": "admin_panel",
      "actions": ["READ", "WRITE", "DELETE"],
      "granted": true
    },
    {
      "resource": "sensor_data",
      "actions": ["READ", "WRITE"],
      "granted": true
    },
    {
      "resource": "alerts",
      "actions": ["READ", "WRITE"],
      "granted": true
    },
    {
      "resource": "notifications",
      "actions": ["READ", "WRITE"],
      "granted": true
    }
  ],
  "timestamp": "2026-04-17T14:35:25Z"
}
```

---

## 📊 EJEMPLO 3: SENSORES

### Caso: Obtener todas las lecturas

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTAxIn0.token"

curl -X GET "http://localhost:8082/api/sensors/readings" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nLatencia: %{time_total}s\n"
```

**Response (200 OK):**
```json
{
  "timestamp": "2026-04-17T14:35:30Z",
  "sensors": [
    {
      "sensorId": "sensor_1",
      "location": "Facility A - Room 101",
      "type": "TEMPERATURE",
      "value": 42.5,
      "unit": "celsius",
      "reading_timestamp": "2026-04-17T14:35:30Z",
      "status": "NORMAL",
      "lastUpdated": "2026-04-17T14:35:30Z"
    },
    {
      "sensorId": "sensor_2",
      "location": "Facility A - Room 102",
      "type": "TEMPERATURE",
      "value": 38.2,
      "unit": "celsius",
      "reading_timestamp": "2026-04-17T14:35:30Z",
      "status": "NORMAL",
      "lastUpdated": "2026-04-17T14:35:30Z"
    },
    {
      "sensorId": "sensor_3",
      "location": "Facility B - Server Room",
      "type": "TEMPERATURE",
      "value": 92.1,
      "unit": "celsius",
      "reading_timestamp": "2026-04-17T14:35:30Z",
      "status": "CRITICAL",
      "lastUpdated": "2026-04-17T14:35:30Z"
    }
  ],
  "count": 3
}
```

**Latencia:** 85ms ✅

**Logs:**
```
[2026-04-17 14:35:30] INFO  SensorAgent - Lecturas generadas: 3 sensores
[2026-04-17 14:35:30] INFO  SensorAgent - Publicando en Kafka: sensor.data
[2026-04-17 14:35:30] INFO  Audit - SENSOR_READ | usuario=user101 | sensores=3 | timestamp=2026-04-17T14:35:30Z
```

---

### Caso: Obtener sensor específico

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.token"

curl -X GET "http://localhost:8082/api/sensors/sensor_3" \
  -H "Authorization: Bearer $TOKEN"
```

**Response (200 OK):**
```json
{
  "sensorId": "sensor_3",
  "location": "Facility B - Server Room",
  "type": "TEMPERATURE",
  "value": 92.1,
  "unit": "celsius",
  "reading_timestamp": "2026-04-17T14:35:30Z",
  "status": "CRITICAL",
  "thresholdWarning": 75,
  "thresholdCritical": 90,
  "history": [
    {
      "value": 91.8,
      "timestamp": "2026-04-17T14:35:20Z"
    },
    {
      "value": 91.5,
      "timestamp": "2026-04-17T14:35:10Z"
    }
  ]
}
```

---

## 🚨 EJEMPLO 4: ALERTAS

### Caso: Obtener alertas activas

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.token"

curl -X GET "http://localhost:8083/api/alerts/active" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nLatencia: %{time_total}s\n"
```

**Response (200 OK):**
```json
{
  "timestamp": "2026-04-17T14:35:35Z",
  "alerts": [
    {
      "alertId": "ALT-001-20260417",
      "sensorId": "sensor_3",
      "sensorValue": 92.1,
      "threshold": 90,
      "severity": "CRITICAL",
      "status": "ACTIVE",
      "detectedAt": "2026-04-17T14:35:30Z",
      "message": "Temperatura crítica detectada en Server Room",
      "recommendedAction": "Aumentar refrigeración inmediatamente"
    }
  ],
  "count": 1,
  "criticalCount": 1,
  "anomalyCount": 0
}
```

**Latencia:** 120ms ✅

**Flujo en tiempo real:**

```
T=0.0s: SensorAgent genera lectura sensor_3 = 92.1°C
        └─ Publica en Kafka: sensor.data

T=0.2s: AlertAgent recibe evento
        ├─ Valida: 92.1 > 90 (CRITICAL)
        ├─ Verifica deduplicación
        └─ Publica: alert.triggered

T=0.3s: NotificationAgent recibe alerta
        ├─ Construye email
        ├─ Envía a admin@stark.com
        └─ Publica: notification.sent

T=0.4s: Auditoría registra
        ├─ Evento de sensor
        ├─ Alerta generada
        └─ Notificación enviada

Total: 400ms desde lectura a notificación ✅
```

---

## 📧 EJEMPLO 5: NOTIFICACIONES

### Caso: Obtener historial de notificaciones

**Request:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.token"

curl -X GET "http://localhost:8085/api/notifications/history?limit=10" \
  -H "Authorization: Bearer $TOKEN"
```

**Response (200 OK):**
```json
{
  "timestamp": "2026-04-17T14:35:40Z",
  "notifications": [
    {
      "notificationId": "NOT-001-20260417",
      "type": "CRITICAL_ALERT",
      "recipient": "admin@stark.com",
      "subject": "[CRÍTICA] Alerta de Temperatura - Server Room",
      "status": "SENT",
      "sentAt": "2026-04-17T14:35:32Z",
      "attempts": 1,
      "relatedAlertId": "ALT-001-20260417"
    },
    {
      "notificationId": "NOT-002-20260417",
      "type": "ACCESS_DENIED",
      "recipient": "security@stark.com",
      "subject": "Intento de acceso denegado - admin_panel",
      "status": "SENT",
      "sentAt": "2026-04-17T14:35:20Z",
      "attempts": 1,
      "relatedUserId": "user101"
    }
  ],
  "count": 2
}
```

---

## 🔄 EJEMPLO 6: FLUJO COMPLETO (End-to-End)

### Escenario: Detección y respuesta a alerta crítica

**Paso 1: Inicio del sistema**
```bash
# Iniciar todo
docker-compose -f docker-compose-sdd.yml up -d

# Esperar a que esté listo
sleep 30

# Verificar salud
curl http://localhost:8761/actuator/health
```

**Paso 2: Usuario se autentica**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@Secure2024!"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

**Paso 3: Obtener lecturas de sensores**
```bash
curl -X GET http://localhost:8082/api/sensors/readings \
  -H "Authorization: Bearer $TOKEN" | jq '.sensors[] | {sensorId, value, status}'
```

**Paso 4: Esperar alerta (sensor_3 sube a 92°C)**
```bash
# Monitorear alertas cada 5 segundos
for i in {1..20}; do
  echo "=== Verificación $i ==="
  curl -s -X GET http://localhost:8083/api/alerts/active \
    -H "Authorization: Bearer $TOKEN" | jq '.alerts[] | {alertId, severity, message}'
  sleep 5
done
```

**Paso 5: Ver notificaciones enviadas**
```bash
curl -X GET http://localhost:8085/api/notifications/history?limit=5 \
  -H "Authorization: Bearer $TOKEN" | jq '.notifications[] | {notificationId, type, status, sentAt}'
```

**Paso 6: Verificar auditoría completa**
```bash
# Ver logs del contenedor
docker-compose logs auth-service | grep "Audit" | head -20
docker-compose logs access-service | grep "Audit" | head -20
docker-compose logs alert-service | grep "Audit" | head -20
```

---

## 📈 EJEMPLO 7: MÉTRICAS Y MONITOREO

### Obtener métricas del sistema

**Request:**
```bash
curl -X GET "http://localhost:8080/actuator/metrics" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "jvm.threads.live",
    "http.server.requests",
    "process.cpu.usage",
    "auth.login.count",
    "auth.login.failures",
    "access.checks.count",
    "sensor.readings.count",
    "alert.triggered.count"
  ]
}
```

### Obtener métrica específica

**Request:**
```bash
curl -X GET "http://localhost:8080/actuator/metrics/http.server.requests" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "name": "http.server.requests",
  "description": "HTTP requests",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1234
    },
    {
      "statistic": "TOTAL",
      "value": 351.234
    },
    {
      "statistic": "MAX",
      "value": 0.856
    }
  ],
  "availableTags": [
    {
      "tag": "method",
      "values": ["GET", "POST", "PUT", "DELETE"]
    }
  ]
}
```

---

## 🧪 EJEMPLO 8: TESTING

### Ejecutar todos los tests

```bash
mvn test
```

**Output:**
```
[INFO] Running com.distribuidos.stark.agent.ServiceAgentTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s

[INFO] Running com.distribuidos.stark.orchestrator.ServiceOrchestratorTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.890 s

[INFO] Running com.distribuidos.stark.auth.AuthAgentTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.234 s

[INFO] Running com.distribuidos.stark.access.AccessControlAgentTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.123 s

[INFO] Running com.distribuidos.stark.integration.EndToEndIntegrationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.456 s

[INFO] Running com.distribuidos.stark.performance.PerformanceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.678 s

[INFO] ========================================
[INFO] Total Tests Run: 48
[INFO] Failures: 0
[INFO] Errors: 0
[INFO] Success Rate: 100%
[INFO] ========================================
```

---

## ⚠️ TROUBLESHOOTING

### Problema: Puerto 8080 ya en uso

```bash
# Encontrar qué está usando el puerto
lsof -i :8080

# Liberar el puerto
kill -9 <PID>

# O cambiar el puerto en application.properties
server.port=8081
```

### Problema: Base de datos no conecta

```bash
# Verificar que PostgreSQL está corriendo
docker-compose ps postgres

# Ver logs
docker-compose logs postgres

# Reiniciar
docker-compose restart postgres
```

### Problema: Kafka sin mensajes

```bash
# Verificar que Kafka está corriendo
docker-compose ps kafka

# Listar topics
docker exec stark-kafka kafka-topics.sh --bootstrap-server kafka:9092 --list

# Ver mensajes en topic
docker exec stark-kafka kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic sensor.data \
  --from-beginning
```

---

**Última actualización:** 17 de Abril de 2026  
**Status:** ✅ VERIFICADO  
**Próxima revisión:** 24 de Abril de 2026


