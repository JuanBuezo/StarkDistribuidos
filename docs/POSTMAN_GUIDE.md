# 📮 Guía de Testing con Postman - Stark Industries

## 🎯 Descripción General

Esta guía te ayudará a probar todos los endpoints del sistema Stark Industries usando Postman.

---

## 📥 OPCIÓN 1: Importar la Colección (Recomendado)

### Paso 1: Descargar el archivo
- Archivo: `docs/Postman_Collection.json`
- Ubicación: Raíz del proyecto

### Paso 2: Importar en Postman
1. Abre **Postman**
2. Click en **Import** (esquina superior izquierda)
3. Selecciona **Upload Files**
4. Elige `Postman_Collection.json`
5. Click en **Import**

### Paso 3: Verificar que se importó correctamente
Deberías ver una carpeta con estructura:
```
Stark Industries - Microservicios Distribuidos/
├── 1. AUTENTICACIÓN
├── 2. GATEWAY
├── 3. SENSORES (CRUD)
├── 4. ALERTAS (CRUD)
├── 5. CONTROL DE ACCESO
├── 6. NOTIFICACIONES
└── 7. SISTEMA (Health & Metrics)
```

---

## 🚀 OPCIÓN 2: Usar el Prompt para la IA

Si prefieres que una IA genere la colección personalizada:

1. Abre el archivo: `docs/POSTMAN_PROMPT_IA.md`
2. Copia todo el contenido
3. Pégalo en ChatGPT, Claude o tu IA favorita
4. Espera a que genere el JSON de Postman
5. Copia el JSON generado
6. En Postman → Import → Paste Raw Text → Pega el JSON

---

## 🔧 CONFIGURACIÓN PREVIA

### 1. Asegúrate que los servicios están corriendo:
```powershell
.\scripts\start-system-fixed.ps1
```

### 2. Espera a que se inicien (30-40 segundos)

### 3. Verifica puertos:
- **Eureka**: http://localhost:8761 (admin/admin123)
- **Gateway**: http://localhost:8080
- **Sensors**: http://localhost:8082
- **Alerts**: http://localhost:8083
- **Access**: http://localhost:8084

---

## 📋 ORDEN DE EJECUCIÓN RECOMENDADO

### FASE 1️⃣: AUTENTICACIÓN (CRÍTICO - Hacer primero)

**1.1 Login (Obtener Token)**
- Método: `POST`
- URL: `http://localhost:8080/auth/login`
- Body:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **⭐ IMPORTANTE**: El token se guarda automáticamente en `{{token}}`
- Response esperada: Code 200 + JWT Token

**1.2 Test Auth Service**
- Método: `GET`
- URL: `http://localhost:8080/auth/test`
- Response: "Auth Service is running!"

**1.3 Validar Token**
- Método: `GET`
- URL: `http://localhost:8080/auth/validate/{{token}}`
- Response: `true`

---

### FASE 2️⃣: GATEWAY (Verificar puente)

**2.1 Gateway Status**
- Método: `GET`
- URL: `http://localhost:8080/`
- Response: Información del Gateway

**2.2 Gateway Rutas**
- Método: `GET`
- URL: `http://localhost:8080/stark`
- Response: Rutas disponibles

**2.3 Health Check**
- Método: `GET`
- URL: `http://localhost:8080/actuator/health`
- Response: `{"status": "UP"}`

---

### FASE 3️⃣: SENSORES (CRUD Completo)

**Primero GET (lectura):**
1. **3.1** GET `/api/sensors` → Todos
2. **3.2** GET `/api/sensors/active` → Solo activos
3. **3.3** GET `/api/sensors/1` → Por ID
4. **3.4** GET `/api/sensors/name/SensorPrincipal` → Por nombre
5. **3.5** GET `/api/sensors/type/MOTION` → Por tipo
6. **3.6** GET `/api/sensors/location/Entrada` → Por ubicación
7. **3.7** GET `/api/sensors/failing?threshold=3` → Con fallos
8. **3.8** GET `/api/sensors/stats` → Estadísticas

**Luego POST/PUT/DELETE:**
9. **3.9** POST → Crear nuevo sensor
10. **3.10** PUT → Actualizar sensor
11. **3.11** PUT `/data` → Actualizar datos
12. **3.12** PUT `/reset-failures` → Resetear fallos
13. **3.13** DELETE → Eliminar sensor

---

### FASE 4️⃣: ALERTAS (CRUD)

**GET primero:**
1. **4.1** GET `/api/alerts` → Todas
2. **4.2** GET `/api/alerts/1` → Por ID
3. **4.3** GET `/api/alerts/level/CRITICAL` → Por nivel

**Luego escribir:**
4. **4.4** POST → Crear alerta
5. **4.5** PUT `/acknowledge` → Marcar revisada
6. **4.6** DELETE → Eliminar alerta

---

### FASE 5️⃣: CONTROL DE ACCESO (Auditoría)

1. **5.1** GET `/api/access` → Todos los registros
2. **5.2** GET `/api/access/failed` → Intentos fallidos
3. **5.3** POST `/api/access` → Registrar acceso
4. **5.4** POST `/api/access/audit/admin` → Auditoría
5. **5.5** GET `/api/access/report` → Reporte

---

### FASE 6️⃣: NOTIFICACIONES

1. **6.1** GET `/api/notifications` → Obtener
2. **6.2** POST `/api/notifications` → Crear

---

### FASE 7️⃣: SISTEMA (Métricas)

1. **7.1** GET `/api/system/status` → Estado
2. **7.2** GET `/api/system/health` → Health check
3. **7.3** GET `/api/system/info` → Información
4. **7.4** GET `/api/system/metrics` → Métricas

---

## ⚠️ PROBLEMAS COMUNES Y SOLUCIONES

### Error: "Cannot GET /auth/login"
**Causa**: Gateway o Auth Service no está activo  
**Solución**: 
```powershell
.\scripts\stop-system.ps1
.\scripts\fix-permissions.ps1
.\scripts\start-system-fixed.ps1
# Espera 30-40 segundos
```

### Error: "401 Unauthorized"
**Causa**: Token inválido o no incluido  
**Solución**: 
1. Ejecuta primero **1.1 Login**
2. Verifica que en el header hay: `Authorization: Bearer {{token}}`

### Error: "404 Not Found"
**Causa**: Endpoint no existe o está mal escrito  
**Solución**: 
1. Verifica la URL exacta
2. Verifica que los parámetros están correctos
3. Consulta la documentación

### El token no se guarda automáticamente
**Causa**: El script de test no ejecutó  
**Solución**:
1. Copia el token manualmente de la respuesta
2. Ve a **Environment** (esquina superior derecha)
3. Haz click en el nombre del ambiente
4. Busca `token`
5. Pega el valor manualmente

---

## 🛠️ CONSEJOS DE TESTING

### 1. Usar variables de entorno
- Los valores precargados están en `{{variable}}`
- Puedes modificarlos en la pestaña Environment

### 2. Pre-requests y Tests
- Cada request puede tener scripts antes/después
- Úsalos para validaciones automáticas

### 3. Exportar resultados
- Tools → Generate Report
- Útil para documentación

### 4. Usar ejemplos
- Cada request tiene un Body example
- Cópialos y adapta los datos

---

## 📊 FLUJO COMPLETO DE TESTING (15 minutos)

```
1. Compila el proyecto (2 min)
   .\mvnw clean install

2. Inicia los servicios (3 min)
   .\scripts\start-system-fixed.ps1
   Espera 30-40 segundos

3. Importa Postman Collection (1 min)
   Import Postman_Collection.json

4. Ejecuta en orden:
   - AUTENTICACIÓN (obtener token)
   - GATEWAY (verificar)
   - SENSORES (CRUD)
   - ALERTAS (CRUD)
   - ACCESO (auditoría)
   - NOTIFICACIONES
   - SISTEMA (métricas)

5. Verifica respuestas (5 min)
   - Todos los 200 OK
   - Datos coherentes

6. Detén servicios (opcional)
   .\scripts\stop-system.ps1
```

---

## 🔐 NOTAS DE SEGURIDAD

1. **Token JWT**: Tiene expiración (24 horas en desarrollo)
2. **Credenciales**: admin/admin123 solo para desarrollo
3. **CORS**: Habilitado en todos los endpoints para testing
4. **Roles requeridos**:
   - ADMIN: Crear, modificar, eliminar
   - SECURITY: Lectura de sensores y alertas
   - Público: Login, test, health

---

## 📝 EJEMPLO: Crear sensor completo

```
1. Login → obtener token
2. GET /api/sensors → ver ID del último sensor
3. POST /api/sensors → crear nuevo
4. GET /api/sensors/{id} → verificar creación
5. PUT /api/sensors/{id} → actualizar
6. GET /api/sensors/stats → ver cambios en estadísticas
7. DELETE /api/sensors/{id} → eliminar
8. GET /api/sensors → confirmar eliminación
```

---

## 🎓 Aprende más

- Postman Docs: https://learning.postman.com/
- Spring REST: https://spring.io/guides/gs/rest-service/
- JWT: https://jwt.io/

---

**¡Listo para testing!** 🚀


