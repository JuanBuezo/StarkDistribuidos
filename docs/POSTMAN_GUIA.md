# 🚀 Guía de Testing con Postman - Stark Microservicios

## ✅ URLS CORRECTAS PARA POSTMAN

⚠️ **IMPORTANTE:** La URL correcta es `/stark/` (sin `-security`)

---

## 🔑 PASO 1: Obtener Token JWT (LOGIN)

### En Postman:

1. **Crear nuevo Request**
   - Método: **POST**
   - URL: `http://localhost:8080/stark/api/auth/login`

2. **Ir a Tab: Body**
   - Seleccionar: **raw**
   - Seleccionar: **JSON**

3. **Pegar este código:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

4. **Click en SEND**

5. **Resultado esperado (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "email": "admin@starkindustries.com",
  "expiresIn": 86400000
}
```

6. **COPIA el token completo (sin comillas)**

---

## 📡 PASO 2: Usar el Token en Otros Requests

### En TODOS los otros requests:

1. **Ir a Tab: Headers**
2. **Agregar:**
   - **Key:** `Authorization`
   - **Value:** `Bearer ` + tu_token

   **Ejemplo:**
   ```
   Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMTI3MDAwMCwiZXhwIjoxNzExMzU2NDAwfQ.abc123...
   ```

3. **IMPORTANTE:** Asegúrate de poner `Bearer ` (con espacio) antes del token

---

## 📋 ENDPOINTS PARA PROBAR

### 🎯 1. GET SENSORES
```
GET http://localhost:8080/stark/api/sensors
Header: Authorization: Bearer <token>
```

**Resultado esperado:** Lista vacía o sensores existentes
```json
[]
```

---

### 🎯 2. CREAR SENSOR
```
POST http://localhost:8080/stark/api/sensors
Header: Authorization: Bearer <token>
Content-Type: application/json

Body (raw):
{
  "sensorId": "SENSOR-MOTION-001",
  "type": "MOTION",
  "location": "Main Hall",
  "active": true
}
```

**Resultado esperado (201 Created):**
```json
{
  "id": 1,
  "sensorId": "SENSOR-MOTION-001",
  "type": "MOTION",
  "location": "Main Hall",
  "active": true,
  "lastValue": null,
  "lastReading": null,
  "status": "OPERATIONAL"
}
```

---

### 🎯 3. GET ALERTAS
```
GET http://localhost:8080/stark/api/alerts
Header: Authorization: Bearer <token>
```

---

### 🎯 4. CREAR ALERTA
```
POST http://localhost:8080/stark/api/alerts
Header: Authorization: Bearer <token>
Content-Type: application/json

Body (raw):
{
  "sensorId": "SENSOR-MOTION-001",
  "alertType": "MOTION",
  "severity": "HIGH",
  "message": "Movimiento detectado en Main Hall",
  "location": "Main Hall",
  "triggerValue": 1.0
}
```

---

### 🎯 5. REGISTRAR ACCESO
```
POST http://localhost:8080/stark/api/access
Header: Authorization: Bearer <token>
Content-Type: application/json

Body (raw):
{
  "userId": "USER-001",
  "location": "Main Hall",
  "action": "ENTRY",
  "allowed": true,
  "deviceId": "DEVICE-001",
  "ipAddress": "192.168.1.100"
}
```

---

### 🎯 6. ENVIAR EMAIL
```
POST http://localhost:8080/stark/api/notifications/email
Header: Authorization: Bearer <token>
Content-Type: application/json

Body (raw):
{
  "recipient": "admin@example.com",
  "subject": "Alerta de Seguridad",
  "message": "Se detectó actividad sospechosa"
}
```

---

## ✅ CHECKLIST PARA DEBUG

Si recibes **404 Not Found**, verifica:

- [ ] ¿Están corriendo todos los servicios? `docker-compose ps`
- [ ] ¿Eureka está disponible? `http://localhost:8761/eureka`
- [ ] ¿Usas `/stark/` y no `/stark-security/`?
- [ ] ¿Incluiste el `Authorization: Bearer <token>` en headers?
- [ ] ¿El token no está expirado?
- [ ] ¿El método HTTP es correcto? (GET, POST, etc)

---

## 🚀 URLS DE REFERENCIA

| Servicio | URL | Método |
|----------|-----|--------|
| **Eureka Dashboard** | http://localhost:8761/eureka | GET |
| **Gateway Health** | http://localhost:8080/stark/actuator/health | GET |
| **Login** | http://localhost:8080/stark/api/auth/login | POST |
| **Sensores** | http://localhost:8080/stark/api/sensors | GET/POST |
| **Alertas** | http://localhost:8080/stark/api/alerts | GET/POST |
| **Acceso** | http://localhost:8080/stark/api/access | GET/POST |
| **Notificaciones** | http://localhost:8080/stark/api/notifications/email | POST |

---

## 🐳 VERIFICAR SERVICIOS

```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs del gateway
docker-compose logs -f gateway

# Ver logs del eureka
docker-compose logs -f eureka-server

# Entrar a un contenedor
docker exec -it stark-gateway bash
```

---

## 💡 TIPS

- **Guarda las variables:** En Postman, usa `{{base_url}}` = `http://localhost:8080/stark`
- **Auto-login:** En el tab "Tests" del login, añade script para guardar token
- **Colecciones:** Organiza todos tus requests en una colección

---

**¡Ahora deberías poder probar sin errores 404!** ✅

