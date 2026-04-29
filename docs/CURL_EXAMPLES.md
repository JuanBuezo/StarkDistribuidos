# 🔌 Testing con cURL (Alternativa a Postman)

Si prefieres testing desde línea de comandos sin Postman, usa estos comandos cURL.

---

## 📋 PASO 0: Guardar Token

```powershell
# Obtener token (guarda en variable)
$token = (curl.exe -X POST http://localhost:8080/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"admin123"}' | 
  ConvertFrom-Json).token

echo $token
```

---

## 🔐 AUTENTICACIÓN

### 1.1 Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 1.2 Test Auth
```bash
curl -X GET http://localhost:8080/auth/test
```

### 1.3 Validar Token
```bash
curl -X GET http://localhost:8080/auth/validate/TOKEN_AQUI
```

---

## 🌉 GATEWAY

### 2.1 Status
```bash
curl -X GET http://localhost:8080/
```

### 2.2 Rutas
```bash
curl -X GET http://localhost:8080/stark
```

### 2.3 Health
```bash
curl -X GET http://localhost:8080/actuator/health
```

---

## 📡 SENSORES (Reemplaza TOKEN_AQUI con tu token)

### GET - Todos
```bash
curl -X GET http://localhost:8080/api/sensors \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Activos
```bash
curl -X GET http://localhost:8080/api/sensors/active \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por ID
```bash
curl -X GET http://localhost:8080/api/sensors/1 \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por nombre
```bash
curl -X GET http://localhost:8080/api/sensors/name/SensorPrincipal \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por tipo
```bash
curl -X GET http://localhost:8080/api/sensors/type/MOTION \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por ubicación
```bash
curl -X GET http://localhost:8080/api/sensors/location/Entrada \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Con fallos
```bash
curl -X GET "http://localhost:8080/api/sensors/failing?threshold=3" \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Estadísticas
```bash
curl -X GET http://localhost:8080/api/sensors/stats \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### POST - Crear sensor
```bash
curl -X POST http://localhost:8080/api/sensors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "name": "SensorNuevo",
    "type": "TEMPERATURE",
    "location": "Almacén",
    "active": true,
    "lastReading": 25.5
  }'
```

### PUT - Actualizar sensor
```bash
curl -X PUT http://localhost:8080/api/sensors/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "name": "SensorActualizado",
    "type": "TEMPERATURE",
    "location": "Almacén Principal",
    "active": true,
    "lastReading": 26.0
  }'
```

### PUT - Actualizar datos
```bash
curl -X PUT "http://localhost:8080/api/sensors/1/data?value=28.5&status=ACTIVE" \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### PUT - Resetear fallos
```bash
curl -X PUT http://localhost:8080/api/sensors/1/reset-failures \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### DELETE - Eliminar sensor
```bash
curl -X DELETE http://localhost:8080/api/sensors/1 \
  -H "Authorization: Bearer TOKEN_AQUI"
```

---

## 🚨 ALERTAS

### GET - Todas
```bash
curl -X GET http://localhost:8080/api/alerts \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por ID
```bash
curl -X GET http://localhost:8080/api/alerts/1 \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Por nivel
```bash
curl -X GET http://localhost:8080/api/alerts/level/CRITICAL \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### POST - Crear alerta
```bash
curl -X POST http://localhost:8080/api/alerts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "title": "Alerta de Temperatura",
    "description": "Temperatura excesiva detectada",
    "level": "WARNING",
    "sensorId": 1,
    "acknowledged": false
  }'
```

### PUT - Marcar revisada
```bash
curl -X PUT http://localhost:8080/api/alerts/1/acknowledge \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### DELETE - Eliminar alerta
```bash
curl -X DELETE http://localhost:8080/api/alerts/1 \
  -H "Authorization: Bearer TOKEN_AQUI"
```

---

## 🔑 CONTROL DE ACCESO

### GET - Todos
```bash
curl -X GET http://localhost:8080/api/access \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Fallidos
```bash
curl -X GET http://localhost:8080/api/access/failed \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### POST - Registrar acceso
```bash
curl -X POST http://localhost:8080/api/access \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "username": "usuario_test",
    "action": "LOGIN",
    "result": "SUCCESS",
    "timestamp": "2024-04-17T12:00:00"
  }'
```

### POST - Auditoría
```bash
curl -X POST http://localhost:8080/api/access/audit/admin \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### GET - Reporte
```bash
curl -X GET http://localhost:8080/api/access/report \
  -H "Authorization: Bearer TOKEN_AQUI"
```

---

## 📬 NOTIFICACIONES

### GET
```bash
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### POST - Crear
```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "userId": "admin",
    "message": "Notificación de prueba",
    "type": "ALERT",
    "read": false
  }'
```

---

## 📊 SISTEMA

### Status
```bash
curl -X GET http://localhost:8080/api/system/status \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### Health
```bash
curl -X GET http://localhost:8080/api/system/health \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### Info
```bash
curl -X GET http://localhost:8080/api/system/info \
  -H "Authorization: Bearer TOKEN_AQUI"
```

### Métricas
```bash
curl -X GET http://localhost:8080/api/system/metrics \
  -H "Authorization: Bearer TOKEN_AQUI"
```

---

## 🛠️ SCRIPT BASH COMPLETO (Linux/Mac)

```bash
#!/bin/bash

# Obtener token
echo "📝 Obteniendo token..."
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"
echo ""

# Test
echo "✅ Test Auth Service"
curl -s -X GET http://localhost:8080/auth/test | jq '.'
echo ""

# Listar sensores
echo "📡 Listando sensores"
curl -s -X GET http://localhost:8080/api/sensors \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# Crear sensor
echo "🆕 Creando nuevo sensor"
curl -s -X POST http://localhost:8080/api/sensors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "SensorTest",
    "type": "MOTION",
    "location": "TestLocation",
    "active": true,
    "lastReading": 100.0
  }' | jq '.'
echo ""

# Obtener estadísticas
echo "📊 Estadísticas de sensores"
curl -s -X GET http://localhost:8080/api/sensors/stats \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

Guarda como `test.sh` y ejecuta:
```bash
chmod +x test.sh
./test.sh
```

---

## 🛠️ SCRIPT POWERSHELL (Windows)

```powershell
# Obtener token
$loginResponse = curl.exe -X POST http://localhost:8080/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"admin123"}' | ConvertFrom-Json

$token = $loginResponse.token
Write-Host "Token: $token" -ForegroundColor Green

# Test
Write-Host "✅ Test Auth Service" -ForegroundColor Yellow
curl.exe -X GET http://localhost:8080/auth/test

# Listar sensores
Write-Host "`n📡 Listando sensores" -ForegroundColor Yellow
curl.exe -X GET http://localhost:8080/api/sensors `
  -H "Authorization: Bearer $token"

# Crear sensor
Write-Host "`n🆕 Creando nuevo sensor" -ForegroundColor Yellow
curl.exe -X POST http://localhost:8080/api/sensors `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token" `
  -d '{"name":"SensorTest","type":"MOTION","location":"TestLocation","active":true,"lastReading":100.0}'

# Obtener estadísticas
Write-Host "`n📊 Estadísticas" -ForegroundColor Yellow
curl.exe -X GET http://localhost:8080/api/sensors/stats `
  -H "Authorization: Bearer $token"
```

Guarda como `test.ps1` y ejecuta:
```powershell
.\test.ps1
```

---

## 🔍 PARSING JSON EN PowerShell

```powershell
# Obtener valor específico
$response = curl.exe -X GET http://localhost:8080/api/sensors `
  -H "Authorization: Bearer $token" | ConvertFrom-Json

# Listar solo nombres
$response | ForEach-Object { Write-Host $_.name }

# Contar registros
Write-Host "Total: $($response.Count)"
```

---

## ✨ TIPS

1. **Guardar respuestas**: Usa `| Out-File respuesta.json`
2. **Pretty print**: Usa `| jq '.'` (si tienes jq instalado)
3. **Exportar variables**: `$token` en PowerShell, `$TOKEN` en Bash
4. **Timeout**: Agrega `-m 10` (10 segundos)
5. **Verbose**: Agrega `-v` para ver headers

---

**Última actualización**: Abril 2026

