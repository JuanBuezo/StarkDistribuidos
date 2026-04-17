# Frontend - Aplicación Web Interactiva

## 📋 Descripción

Se ha agregado un **frontend moderno y responsivo** que incluye:

✅ **Sistema de Autenticación**
- Login seguro con HTTP Basic Auth
- Registro de nuevos usuarios
- Gestión de sesiones en localStorage
- Recuperación automática de sesión

✅ **Dashboard de Telemetría** (Inspirado en Grafana)
- 📊 Estadísticas en tiempo real
- 📈 Gráficos interactivos con Chart.js
- 🔴 Feed en tiempo real de eventos
- 🎯 Múltiples pestañas de información

✅ **Integración con Microservicios**
- Conexión con API REST de Stark
- WebSocket STOMP para notificaciones en tiempo real
- Sincronización automática de datos
- Manejo de errores y reconexión

---

## 🚀 Acceso a la Aplicación

### URL Principal
```
http://localhost:8080/stark-security/
```

### Credenciales de Demo
| Usuario | Contraseña | Roles |
|---------|-----------|-------|
| admin | admin123 | ADMIN, SECURITY, USER |
| security | security123 | SECURITY, USER |
| user | user123 | USER |

---

## 📁 Estructura de Archivos

```
src/main/resources/static/
├── index.html                 # Página principal
├── styles/
│   └── style.css             # Estilos CSS modernos
├── js/
│   ├── app.js                # Funcionalidad principal y utilidades
│   ├── auth.js               # Sistema de autenticación
│   ├── dashboard.js          # Lógica del dashboard y gráficos
│   └── websocket.js          # Comunicación en tiempo real
└── images/
    └── (iconos y recursos)
```

---

## 🔐 Autenticación

### Login
1. Accede a `http://localhost:8080/stark-security/`
2. Ingresa usuario y contraseña
3. El token se almacena en `localStorage` como `authToken`
4. La sesión se mantiene entre recargas de página

### HTTP Basic Auth
```javascript
// El frontend codifica automáticamente las credenciales
const token = btoa('usuario:contraseña');
// Luego se envía en cada solicitud:
headers['Authorization'] = 'Basic ' + token;
```

### Cierre de Sesión
- Click en botón "Salir" en la esquina superior derecha
- Se limpian todas las credenciales guardadas
- Se desconecta del WebSocket

---

## 📊 Dashboard - Pestañas

### 1. **Overview** (Página Principal)
Muestra:
- Estadísticas de sensores activos
- Conteo de alertas activas
- Alertas críticas
- Estado del sistema
- Gráficos de tendencias
- Feed en tiempo real de eventos

### 2. **Sensores**
Funcionalidades:
- Listar todos los sensores
- Ver detalles: nombre, tipo, ubicación, valor actual
- Estado del sensor (Activo/Inactivo)
- Crear nuevos sensores (solo ADMIN/SECURITY)
- Última actualización de datos

### 3. **Alertas**
Funcionalidades:
- Listar alertas no reconocidas
- Filtrar por nivel (Crítica, Advertencia, Info)
- Ver detalles: ID, sensor, mensaje, timestamp
- Reconocer alertas
- Actualizar lista en tiempo real

### 4. **Acceso**
Funcionalidades:
- Ver logs de intentos de acceso
- Usuario que intentó acceder
- Sensor al que intentó acceder
- Resultado (Permitido/Denegado)
- Dirección IP y timestamp

---

## 📡 API REST Endpoints Utilizados

El frontend se comunica con los siguientes endpoints:

```bash
# SENSORES
GET    /api/sensors              # Listar sensores
POST   /api/sensors              # Crear sensor

# ALERTAS
GET    /api/alerts               # Listar todas las alertas
GET    /api/alerts/unacknowledged # Alertas no reconocidas
GET    /api/alerts/stats         # Estadísticas de alertas
PUT    /api/alerts/{id}/acknowledge # Reconocer alerta

# ACCESO
GET    /api/access/logs          # Ver logs de acceso

# SISTEMA
GET    /api/system/health        # Estado de salud (sin auth)
GET    /api/system/status        # Estado del sistema (sin auth)
GET    /api/system/metrics       # Métricas del sistema
```

---

## 🔴 WebSocket - Notificaciones en Tiempo Real

El frontend se conecta automáticamente a WebSocket para recibir:

### Topics Suscritos
```javascript
// Alertas en tiempo real
stompClient.subscribe('/topic/alerts', callback);

// Datos de sensores
stompClient.subscribe('/topic/sensor-data', callback);

// Eventos del sistema
stompClient.subscribe('/topic/system-events', callback);
```

### Características
- ✅ Reconexión automática si se pierde la conexión
- ✅ Notificaciones del navegador para alertas críticas
- ✅ Feed en vivo con nuevos eventos
- ✅ Actualización automática de gráficos

---

## 🎨 Características de UI/UX

### Diseño
- 🌙 Tema oscuro moderno (inspirado en sistemas empresariales)
- 📱 **Completamente responsivo** (funciona en móvil, tablet, desktop)
- ⚡ Animaciones suaves y transiciones
- 🎯 Interfaz intuitiva y fácil de usar

### Colores
- **Primario**: Naranja (#FF6B35) - Alertas y acciones
- **Secundario**: Azul oscuro (#004E89) - Información
- **Éxito**: Verde (#2ECC71) - Estados positivos
- **Advertencia**: Amarillo (#F39C12) - Advertencias
- **Peligro**: Rojo (#E74C3C) - Errores críticos

### Gráficos
- Gráficos de línea para tendencias de sensores
- Gráficos de barras para distribución de alertas
- Actualización en tiempo real
- Interactividad con Chart.js

---

## 🔄 Sincronización de Datos

### Actualización Automática
- **Cada 30 segundos**: Recarga de estadísticas
- **WebSocket**: Actualizaciones instantáneas de eventos
- **En tiempo real**: Feed de eventos

### Almacenamiento Local
- `authToken`: Token de autenticación
- `currentUser`: Información del usuario actual

---

## 🛠️ Configuración del Servidor

### Files Estáticos
Servidos desde `/stark-security/static/`:
```
/stark-security/           → Página principal
/stark-security/js/**      → JavaScript
/stark-security/styles/**  → CSS
/stark-security/images/**  → Imágenes
```

### Rutas Protegidas
```java
// Permitidas sin autenticación
/stark-security/
/stark-security/index.html
/stark-security/static/**
/stark-security/ws/**

// Requieren autenticación (HTTP Basic)
/api/sensors/**
/api/alerts/**
/api/access/**
```

---

## 📋 Archivos JavaScript Principales

### **app.js** (Utilidades Generales)
```javascript
// Funciones principales
apiCall()              // Realiza llamadas HTTP autenticadas
encodeBasicAuth()      // Codifica credenciales
formatDate()           // Formatea fechas
toggleForm()           // Alterna entre login/registro
switchTab()            // Cambia entre pestañas del dashboard
showDashboard()        // Muestra el dashboard
logout()               // Cierra sesión
```

### **auth.js** (Autenticación)
```javascript
handleLogin()          // Procesa login
handleRegister()       // Procesa registro
isValidEmail()         // Valida email
```

### **dashboard.js** (Dashboard)
```javascript
loadDashboardData()    // Carga datos iniciales
loadSensorStats()      // Estadísticas de sensores
loadAlertStats()       // Estadísticas de alertas
loadSensors()          // Lista sensores
loadAlerts()           // Lista alertas
loadAccessLogs()       // Lista logs de acceso
initializeCharts()     // Inicializa gráficos
```

### **websocket.js** (Tiempo Real)
```javascript
connectWebSocket()     // Conecta a WebSocket
handleRealtimeAlert()  // Maneja alertas en tiempo real
handleRealtimeSensorData() // Maneja datos de sensores
requestNotificationPermission() // Solicita permisos
```

---

## 🔍 Debugging

### Verificar Tokens
```javascript
// En la consola del navegador
console.log(localStorage.getItem('authToken'));
console.log(localStorage.getItem('currentUser'));
```

### Ver Conexión WebSocket
```javascript
// Estado de STOMP
console.log(stompClient.connected);
```

### Logs de API
Todas las llamadas se logean en la consola del navegador con prefijo "API Error"

---

## 🐛 Solución de Problemas

### "Usuario o contraseña inválidos"
- Verifica que uses: `admin` / `admin123`
- El servidor debe estar ejecutándose en `http://localhost:8080`

### "No se pueden cargar los sensores"
- Verifica que el endpoint `/api/sensors` sea accesible
- Asegúrate de tener rol `SECURITY` o `ADMIN`

### "WebSocket no se conecta"
- Verifica que WebSocket esté habilitado en la configuración
- Comprueba que no haya firewall bloqueando la conexión

### "Gráficos no aparecen"
- Chart.js debe estar cargado desde CDN
- Verifica la consola del navegador para errores

---

## 🚀 Deployment

### Docker
```bash
# Construir imagen
docker build -t stark-security:latest .

# Ejecutar
docker run -p 8080:8080 stark-security:latest

# Acceso
http://localhost:8080/stark-security/
```

### Production
- Los archivos estáticos se cachean por 1 año (`cache-control: max-age=31536000`)
- HTTP/2 habilitado para mejor rendimiento
- Compresión GZIP habilitada
- HTTPS recomendado en producción

---

## 📞 Soporte

Para reportar problemas o sugerencias sobre el frontend:
1. Revisa los logs del navegador (F12 → Console)
2. Verifica que el servidor esté ejecutándose
3. Asegúrate de tener las credenciales correctas
4. Intenta hacer logout y login nuevamente

---

**¡El frontend está listo para usar! 🚀**

*Última actualización: 2026-03-25*

