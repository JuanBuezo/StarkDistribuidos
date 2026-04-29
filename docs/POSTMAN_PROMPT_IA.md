# 🤖 PROMPT PARA IA DE POSTMAN - Crear HTTP Requests

Copia este prompt completo en una IA (ChatGPT, Claude, Copilot, etc.) para que genere una colección Postman con todos los endpoints:

---

## INSTRUCCIONES PARA LA IA:

Necesito que generes una **colección Postman completa** (en formato JSON) para probar todos los endpoints del sistema **Stark Industries - Microservicios Distribuidos**. 

**IMPORTANTE - LEE ESTO ANTES DE GENERAR:**

1. **Sistema base**: Spring Boot 3.3 + Spring Cloud
2. **Servidor**: localhost
3. **Puerto del Gateway**: 8080
4. **Credenciales por defecto**: username: `admin`, password: `admin123`
5. **Autenticación**: JWT Token (Bearer)
6. **CORS habilitado** en todos los servicios

---

## 📋 ENDPOINTS A INCLUIR (en este ORDEN exacto):

### FASE 1: AUTENTICACIÓN (PRIMERO)
1. **POST** `/auth/login` - Obtener JWT Token
   - Body JSON: `{ "username": "admin", "password": "admin123" }`
   - Guarda el token en variable global `{{token}}`

2. **GET** `/auth/test` - Verificar Auth Service activo
   
3. **GET** `/auth/validate/{{token}}` - Validar token

---

### FASE 2: GATEWAY (Verificar puente central)
4. **GET** `/` - Estado del Gateway

5. **GET** `/stark` - Rutas disponibles

6. **GET** `/actuator/health` - Health check del Gateway

---

### FASE 3: SENSORES (Operaciones CRUD)
7. **GET** `/api/sensors` - Obtener todos los sensores
   - Header: `Authorization: Bearer {{token}}`

8. **GET** `/api/sensors/active` - Solo sensores activos
   - Header: `Authorization: Bearer {{token}}`

9. **GET** `/api/sensors/{id}` - Sensor por ID (usar id=1)
   - Header: `Authorization: Bearer {{token}}`

10. **GET** `/api/sensors/name/{name}` - Sensor por nombre (usar name="SensorPrincipal")
    - Header: `Authorization: Bearer {{token}}`

11. **GET** `/api/sensors/type/{type}` - Sensores por tipo (usar type="MOTION")
    - Header: `Authorization: Bearer {{token}}`

12. **GET** `/api/sensors/location/{location}` - Sensores por ubicación (usar location="Entrada")
    - Header: `Authorization: Bearer {{token}}`

13. **GET** `/api/sensors/failing` - Sensores con fallos
    - Header: `Authorization: Bearer {{token}}`
    - Query params: `threshold=3`

14. **GET** `/api/sensors/stats` - Estadísticas de sensores
    - Header: `Authorization: Bearer {{token}}`

15. **POST** `/api/sensors` - Crear nuevo sensor
    - Header: `Authorization: Bearer {{token}}`
    - Body JSON:
    ```json
    {
      "name": "SensorNuevo",
      "type": "TEMPERATURE",
      "location": "Almacén",
      "active": true,
      "lastReading": 25.5
    }
    ```

16. **PUT** `/api/sensors/{id}` - Actualizar sensor (usar id=1)
    - Header: `Authorization: Bearer {{token}}`
    - Body JSON: (mismo que crear con cambios)

17. **PUT** `/api/sensors/{id}/data` - Actualizar datos de sensor
    - Header: `Authorization: Bearer {{token}}`
    - Query params: `value=28.5&status=ACTIVE`

18. **PUT** `/api/sensors/{id}/reset-failures` - Resetear fallos
    - Header: `Authorization: Bearer {{token}}`

19. **DELETE** `/api/sensors/{id}` - Eliminar sensor (usar id existente)
    - Header: `Authorization: Bearer {{token}}`

---

### FASE 4: ALERTAS (CRUD)
20. **GET** `/api/alerts` - Obtener todas las alertas
    - Header: `Authorization: Bearer {{token}}`

21. **GET** `/api/alerts/{id}` - Alerta por ID (usar id=1)
    - Header: `Authorization: Bearer {{token}}`

22. **GET** `/api/alerts/level/{level}` - Alertas por nivel
    - Header: `Authorization: Bearer {{token}}`
    - Usar levels: `CRITICAL`, `WARNING`, `INFO`

23. **POST** `/api/alerts` - Crear nueva alerta
    - Header: `Authorization: Bearer {{token}}`
    - Body JSON:
    ```json
    {
      "title": "Alerta de Temperatura",
      "description": "Temperatura excesiva detectada",
      "level": "WARNING",
      "sensorId": 1,
      "acknowledged": false
    }
    ```

24. **PUT** `/api/alerts/{id}/acknowledge` - Marcar como revisada
    - Header: `Authorization: Bearer {{token}}`

25. **DELETE** `/api/alerts/{id}` - Eliminar alerta
    - Header: `Authorization: Bearer {{token}}`

---

### FASE 5: CONTROL DE ACCESO (Auditoría)
26. **GET** `/api/access` - Obtener todos los registros de acceso
    - Header: `Authorization: Bearer {{token}}`

27. **GET** `/api/access/failed` - Intentos de acceso fallidos
    - Header: `Authorization: Bearer {{token}}`

28. **POST** `/api/access` - Registrar nuevo acceso
    - Header: `Authorization: Bearer {{token}}`
    - Body JSON:
    ```json
    {
      "username": "usuario_test",
      "action": "LOGIN",
      "result": "SUCCESS",
      "timestamp": "2024-04-17T12:00:00"
    }
    ```

29. **POST** `/api/access/audit/{username}` - Auditoría de usuario
    - Header: `Authorization: Bearer {{token}}`

30. **GET** `/api/access/report` - Generar reporte de seguridad
    - Header: `Authorization: Bearer {{token}}`

---

### FASE 6: NOTIFICACIONES
31. **GET** `/api/notifications` - Obtener notificaciones
    - Header: `Authorization: Bearer {{token}}`

32. **POST** `/api/notifications` - Crear notificación
    - Header: `Authorization: Bearer {{token}}`
    - Body JSON:
    ```json
    {
      "userId": "admin",
      "message": "Notificación de prueba",
      "type": "ALERT",
      "read": false
    }
    ```

---

### FASE 7: SISTEMA (Health & Metrics)
33. **GET** `/api/system/status` - Estado del sistema
    - Header: `Authorization: Bearer {{token}}`

34. **GET** `/api/system/health` - Health check completo
    - Header: `Authorization: Bearer {{token}}`

35. **GET** `/api/system/info` - Información del sistema
    - Header: `Authorization: Bearer {{token}}`

36. **GET** `/api/system/metrics` - Métricas del sistema
    - Header: `Authorization: Bearer {{token}}`

---

## 🔧 CONFIGURACIÓN REQUERIDA:

1. **Base URL**: `http://localhost:8080`

2. **Variables globales a usar**:
   - `{{token}}` - JWT del login (se obtiene en FASE 1)
   - `{{host}}` - localhost
   - `{{port}}` - 8080

3. **Headers por defecto en todas las requests**:
   - `Content-Type: application/json`
   - `Authorization: Bearer {{token}}` (excepto login y test)

4. **Timeout**: 5000ms

---

## ⚠️ ORDEN OBLIGATORIO DE EJECUCIÓN:

```
1. Login (POST /auth/login) ← Primero para obtener token
2. Auth Test (GET /auth/test)
3. Validate Token (GET /auth/validate)
4. Gateway endpoints (GET /)
5. Sensores (todos GET primero, luego POST, PUT, DELETE)
6. Alertas (GET primero, luego POST, PUT, DELETE)
7. Access (GET primero, luego POST)
8. Notifications
9. System metrics
```

---

## 📝 FORMATO DE RESPUESTA:

Por favor genera:
1. **Archivo JSON válido** para importar en Postman
2. **Estructura de carpetas** por servicios
3. **Variables de entorno** precargadas
4. **Scripts de test** para validar respuestas
5. **Ejemplos de Body** en cada request

---

## 🎯 EJEMPLO DE ESTRUCTURA ESPERADA:

```
Stark Industries - Test Collection/
├── 1. Autenticación
│   ├── Login
│   ├── Test Auth
│   └── Validate Token
├── 2. Gateway
│   ├── Status
│   ├── Routes
│   └── Health
├── 3. Sensores
│   ├── GET Todos
│   ├── GET por ID
│   ├── POST Crear
│   ├── PUT Actualizar
│   └── DELETE
├── 4. Alertas
│   ├── GET Todas
│   ├── POST Crear
│   ├── PUT Acknowledge
│   └── DELETE
├── 5. Control de Acceso
│   ├── GET Logs
│   ├── POST Registro
│   └── GET Reporte
├── 6. Notificaciones
│   └── ...
└── 7. Sistema
    ├── Status
    ├── Health
    └── Metrics
```

---

## ✅ CHECKLIST FINAL:

- [ ] Todas las requests en JSON válido
- [ ] Variables de entorno definidas
- [ ] Headers correctos por request
- [ ] Body examples incluidos
- [ ] Scripts de extracción de token incluidos
- [ ] Documentación de cada endpoint
- [ ] Tests automáticos para validar respuestas
- [ ] Orden de ejecución respetado

---

**¡Genera la colección Postman completa ahora!**


