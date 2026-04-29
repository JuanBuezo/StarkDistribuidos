# 🔗 Referencia Rápida de Endpoints

**Base URL**: `http://localhost:8080`

**Auth Header**: `Authorization: Bearer {{token}}`  
**Content-Type**: `application/json`

---

## 🔐 AUTENTICACIÓN (Sin token requerido)

| # | Método | Endpoint | Descripción |
|---|--------|----------|-------------|
| 1.1 | POST | `/auth/login` | Obtener JWT token |
| 1.2 | GET | `/auth/test` | Verificar servicio activo |
| 1.3 | GET | `/auth/validate/{token}` | Validar JWT token |

**Body para 1.1:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

---

## 🌉 GATEWAY

| # | Método | Endpoint | Descripción |
|---|--------|----------|-------------|
| 2.1 | GET | `/` | Status del Gateway |
| 2.2 | GET | `/stark` | Rutas disponibles |
| 2.3 | GET | `/actuator/health` | Health check |

---

## 📡 SENSORES (13 endpoints)

### GET (Lectura)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 3.1 | GET | `/api/sensors` | Todos los sensores | ✅ |
| 3.2 | GET | `/api/sensors/active` | Solo activos | ✅ |
| 3.3 | GET | `/api/sensors/{id}` | Por ID | ✅ |
| 3.4 | GET | `/api/sensors/name/{name}` | Por nombre | ✅ |
| 3.5 | GET | `/api/sensors/type/{type}` | Por tipo | ✅ |
| 3.6 | GET | `/api/sensors/location/{location}` | Por ubicación | ✅ |
| 3.7 | GET | `/api/sensors/failing?threshold=3` | Con fallos | ✅ |
| 3.8 | GET | `/api/sensors/stats` | Estadísticas | ✅ |

### POST (Crear)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 3.9 | POST | `/api/sensors` | Crear sensor | ✅ ADMIN |

**Body:**
```json
{
  "name": "SensorNuevo",
  "type": "TEMPERATURE",
  "location": "Almacén",
  "active": true,
  "lastReading": 25.5
}
```

### PUT (Actualizar)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 3.10 | PUT | `/api/sensors/{id}` | Actualizar sensor | ✅ ADMIN |
| 3.11 | PUT | `/api/sensors/{id}/data?value=X&status=Y` | Actualizar datos | ✅ |
| 3.12 | PUT | `/api/sensors/{id}/reset-failures` | Resetear fallos | ✅ ADMIN |

### DELETE (Eliminar)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 3.13 | DELETE | `/api/sensors/{id}` | Eliminar sensor | ✅ ADMIN |

**Tipos disponibles**: MOTION, TEMPERATURE, HUMIDITY, LIGHT, etc.

---

## 🚨 ALERTAS (6 endpoints)

### GET
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 4.1 | GET | `/api/alerts` | Todas las alertas | ✅ |
| 4.2 | GET | `/api/alerts/{id}` | Alerta por ID | ✅ |
| 4.3 | GET | `/api/alerts/level/{level}` | Por nivel | ✅ |

**Niveles**: CRITICAL, WARNING, INFO

### POST (Crear)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 4.4 | POST | `/api/alerts` | Crear alerta | ✅ |

**Body:**
```json
{
  "title": "Alerta de Temperatura",
  "description": "Temperatura excesiva",
  "level": "WARNING",
  "sensorId": 1,
  "acknowledged": false
}
```

### PUT (Actualizar)
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 4.5 | PUT | `/api/alerts/{id}/acknowledge` | Marcar revisada | ✅ |

### DELETE
| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 4.6 | DELETE | `/api/alerts/{id}` | Eliminar alerta | ✅ ADMIN |

---

## 🔑 CONTROL DE ACCESO (5 endpoints)

| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 5.1 | GET | `/api/access` | Todos los registros | ✅ |
| 5.2 | GET | `/api/access/failed` | Intentos fallidos | ✅ |
| 5.3 | POST | `/api/access` | Registrar acceso | ✅ |
| 5.4 | POST | `/api/access/audit/{username}` | Auditoría de usuario | ✅ ADMIN |
| 5.5 | GET | `/api/access/report` | Reporte de seguridad | ✅ ADMIN |

**Body para 5.3:**
```json
{
  "username": "usuario_test",
  "action": "LOGIN",
  "result": "SUCCESS",
  "timestamp": "2024-04-17T12:00:00"
}
```

---

## 📬 NOTIFICACIONES (2 endpoints)

| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 6.1 | GET | `/api/notifications` | Obtener notificaciones | ✅ |
| 6.2 | POST | `/api/notifications` | Crear notificación | ✅ |

**Body para 6.2:**
```json
{
  "userId": "admin",
  "message": "Notificación de prueba",
  "type": "ALERT",
  "read": false
}
```

---

## 📊 SISTEMA (4 endpoints)

| # | Método | Endpoint | Descripción | Auth |
|---|--------|----------|-------------|------|
| 7.1 | GET | `/api/system/status` | Estado del sistema | ✅ |
| 7.2 | GET | `/api/system/health` | Health check completo | ✅ |
| 7.3 | GET | `/api/system/info` | Información técnica | ✅ |
| 7.4 | GET | `/api/system/metrics` | Métricas de rendimiento | ✅ ADMIN |

---

## 📋 RESUMEN POR FASE

```
Total de endpoints: 36+

FASE 1: Autenticación        - 3 endpoints
FASE 2: Gateway              - 3 endpoints
FASE 3: Sensores (CRUD)      - 13 endpoints
FASE 4: Alertas (CRUD)       - 6 endpoints
FASE 5: Control de Acceso    - 5 endpoints
FASE 6: Notificaciones       - 2 endpoints
FASE 7: Sistema              - 4 endpoints
```

---

## 🔄 ORDEN DE EJECUCIÓN

```
1. Ejecuta: 1.1 (Login) ← OBLIGATORIO PRIMERO
2. Ejecuta: 1.2, 1.3 (Auth tests)
3. Ejecuta: 2.1, 2.2, 2.3 (Gateway)
4. Ejecuta: 3.1-3.8 (Sensores GET)
5. Ejecuta: 3.9 (POST Sensor)
6. Ejecuta: 3.10-3.13 (Sensores PUT/DELETE)
7. Ejecuta: 4.1-4.6 (Alertas)
8. Ejecuta: 5.1-5.5 (Acceso)
9. Ejecuta: 6.1-6.2 (Notificaciones)
10. Ejecuta: 7.1-7.4 (Sistema)
```

---

## ⚠️ REQUISITOS

✅ Sistema corriendo: `.\scripts\start-system-fixed.ps1`  
✅ Eureka activo: http://localhost:8761  
✅ Gateway en 8080: http://localhost:8080  
✅ Token obtenido del login  

---

## 🔑 CREDENCIALES

```
Username: admin
Password: admin123
```

---

## 📝 EJEMPLO FLUJO COMPLETO

```bash
# 1. Login
POST /auth/login
Body: {"username":"admin","password":"admin123"}
Response: {"token":"eyJ...","username":"admin",...}

# 2. Listar sensores (guarda el token en {{token}})
GET /api/sensors
Header: Authorization: Bearer {{token}}

# 3. Crear sensor
POST /api/sensors
Header: Authorization: Bearer {{token}}
Body: {"name":"Nuevo","type":"MOTION","location":"Entrada","active":true}

# 4. Actualizar sensor
PUT /api/sensors/1
Header: Authorization: Bearer {{token}}
Body: {"name":"Actualizado",...}

# 5. Obtener estadísticas
GET /api/sensors/stats
Header: Authorization: Bearer {{token}}

# 6. Eliminar
DELETE /api/sensors/1
Header: Authorization: Bearer {{token}}
```

---

**Última actualización**: Abril 2026

