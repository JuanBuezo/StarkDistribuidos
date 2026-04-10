# 🏗️ Arquitectura - Frontend + Backend Integrado

## Diagrama General

```
╔════════════════════════════════════════════════════════════════════════════╗
║                         USUARIO EN NAVEGADOR WEB                           ║
║                                                                              ║
║  ┌────────────────────────────────────────────────────────────────────┐    ║
║  │                    STARK INDUSTRIES DASHBOARD                      │    ║
║  │                                                                    │    ║
║  │  ┌────────────────────────────────────────────────────────────┐  │    ║
║  │  │ LOGIN/REGISTRO                                             │  │    ║
║  │  │ ├─ Formulario de autenticación                            │  │    ║
║  │  │ ├─ Validación de credenciales                            │  │    ║
║  │  │ └─ Almacenamiento en localStorage                        │  │    ║
║  │  └────────────────────────────────────────────────────────────┘  │    ║
║  │                                                                    │    ║
║  │  ┌────────────────────────────────────────────────────────────┐  │    ║
║  │  │ DASHBOARD (5 PESTAÑAS)                                    │  │    ║
║  │  │                                                             │  │    ║
║  │  │ 📊 OVERVIEW                                               │  │    ║
║  │  │    ├─ Tarjetas de estadísticas                           │  │    ║
║  │  │    ├─ Gráficos (Chart.js)                               │  │    ║
║  │  │    └─ Feed de eventos en vivo                           │  │    ║
║  │  │                                                             │  │    ║
║  │  │ 📡 SENSORES                                              │  │    ║
║  │  │    ├─ Lista de sensores                                 │  │    ║
║  │  │    ├─ Crear nuevo sensor                                │  │    ║
║  │  │    └─ Ver detalles y valores                            │  │    ║
║  │  │                                                             │  │    ║
║  │  │ 🚨 ALERTAS                                               │  │    ║
║  │  │    ├─ Tabla de alertas                                  │  │    ║
║  │  │    ├─ Filtrado por nivel                                │  │    ║
║  │  │    └─ Reconocimiento de alertas                         │  │    ║
║  │  │                                                             │  │    ║
║  │  │ 🔐 ACCESO                                                │  │    ║
║  │  │    ├─ Logs de intentos                                  │  │    ║
║  │  │    ├─ Auditoría completa                                │  │    ║
║  │  │    └─ Filtrado por usuario                              │  │    ║
║  │  └────────────────────────────────────────────────────────────┘  │    ║
║  │                                                                    │    ║
║  └────────────────────────────────────────────────────────────────────┘    ║
║                                                                              ║
║  Tecnologías:                                                              ║
║  • HTML5, CSS3, JavaScript Vanilla                                         ║
║  • Chart.js para gráficos interactivos                                    ║
║  • SockJS + STOMP para WebSocket                                         ║
║  • LocalStorage para sesiones                                             ║
║                                                                              ║
╚════════════════════════════════════════════════════════════════════════════╝
             ↓ REST API (HTTP Basic Auth)  ↓ WebSocket STOMP
             ↓                             ↓
╔════════════════════════════════════════════════════════════════════════════╗
║                        SPRING BOOT BACKEND                                 ║
║                                                                              ║
║  ┌────────────────────────────────────────────────────────────────────┐    ║
║  │ CONTROLADORES (REST API)                                           │    ║
║  │                                                                    │    ║
║  │ ✨ FrontendController (NUEVO)                                     │    ║
║  │    GET /stark-security/ ..................... index.html          │    ║
║  │    GET /stark-security/index.html ........... index.html          │    ║
║  │    GET /stark-security/dashboard ........... index.html           │    ║
║  │                                                                    │    ║
║  │ SensorController                                                  │    ║
║  │    GET /stark-security/api/sensors .................. Listar      │    ║
║  │    POST /stark-security/api/sensors ................ Crear       │    ║
║  │    GET /stark-security/api/sensors/{id} .......... Obtener      │    ║
║  │    PUT /stark-security/api/sensors/{id}/data ... Actualizar    │    ║
║  │                                                                    │    ║
║  │ AlertController                                                   │    ║
║  │    GET /stark-security/api/alerts ........... Listar todos       │    ║
║  │    GET /stark-security/api/alerts/unacknowledged ... No leidas  │    ║
║  │    PUT /stark-security/api/alerts/{id}/acknowledge ... Reconocer│    ║
║  │    GET /stark-security/api/alerts/stats .... Estadísticas       │    ║
║  │                                                                    │    ║
║  │ AccessController                                                  │    ║
║  │    POST /stark-security/api/access/log ... Registrar intento    │    ║
║  │    GET /stark-security/api/access/logs ... Ver logs             │    ║
║  │                                                                    │    ║
║  │ SystemController                                                  │    ║
║  │    GET /stark-security/api/system/health ... Estado             │    ║
║  │    GET /stark-security/api/system/status ... Status             │    ║
║  │    GET /stark-security/api/system/metrics .. Métricas           │    ║
║  │                                                                    │    ║
║  └────────────────────────────────────────────────────────────────────┘    ║
║                                                                              ║
║  ┌────────────────────────────────────────────────────────────────────┐    ║
║  │ CONFIGURACIÓN & SEGURIDAD                                          │    ║
║  │                                                                    │    ║
║  │ ✨ WebConfig (NUEVO)                                              │    ║
║  │    └─ Mapeo de recursos estáticos (/static/**, /js/**, etc)     │    ║
║  │                                                                    │    ║
║  │ SecurityConfig (MODIFICADO)                                       │    ║
║  │    ├─ HTTP Basic Auth en APIs                                   │    ║
║  │    ├─ Permisos: /stark-security/** sin auth                    │    ║
║  │    ├─ Permisos: /api/** con auth                               │    ║
║  │    ├─ Roles: ADMIN, SECURITY, USER                             │    ║
║  │    └─ CSRF disabled para APIs                                   │    ║
║  │                                                                    │    ║
║  │ WebSocketConfig                                                   │    ║
║  │    ├─ STOMP endpoint: /ws/notifications                         │    ║
║  │    ├─ Application destinations: /topic/**                       │    ║
║  │    └─ SockJS enablement                                         │    ║
║  │                                                                    │    ║
║  │ AsyncConfig                                                        │    ║
║  │    └─ Procesamiento paralelo de sensores                         │    ║
║  │                                                                    │    ║
║  └────────────────────────────────────────────────────────────────────┘    ║
║                                                                              ║
║  ┌────────────────────────────────────────────────────────────────────┐    ║
║  │ CAPAS DE SERVICIO                                                  │    ║
║  │                                                                    │    ║
║  │ SensorService                                                      │    ║
║  │    ├─ Gestión de sensores                                        │    ║
║  │    ├─ Validación de datos                                        │    ║
║  │    └─ Envío de eventos vía WebSocket                            │    ║
║  │                                                                    │    ║
║  │ AlertService                                                       │    ║
║  │    ├─ Generación de alertas                                      │    ║
║  │    ├─ Notificaciones                                             │    ║
║  │    └─ Reconocimiento de alertas                                  │    ║
║  │                                                                    │    ║
║  │ AccessControlService                                              │    ║
║  │    ├─ Registro de accesos                                        │    ║
║  │    ├─ Auditoría                                                  │    ║
║  │    └─ Reportes                                                   │    ║
║  │                                                                    │    ║
║  │ NotificationService                                               │    ║
║  │    ├─ Envío de emails                                            │    ║
║  │    └─ Notificaciones asincrónicas                                │    ║
║  │                                                                    │    ║
║  └────────────────────────────────────────────────────────────────────┘    ║
║                                                                              ║
╚════════════════════════════════════════════════════════════════════════════╝
             ↓ JDBC / JPA
             ↓
╔════════════════════════════════════════════════════════════════════════════╗
║                      BASE DE DATOS                                         ║
║                                                                              ║
║  H2 (Desarrollo)           │       PostgreSQL (Producción)                 ║
║  ┌─────────────────────┐   │   ┌──────────────────────┐                    ║
║  │ IN-MEMORY DATABASE  │   │   │ PERSISTENT DATABASE  │                    ║
║  ├─────────────────────┤   │   ├──────────────────────┤                    ║
║  │                     │   │   │                      │                    ║
║  │ SENSORS             │   │   │ sensors              │                    ║
║  │ ├─ id (PK)          │   │   │ ├─ id                │                    ║
║  │ ├─ name             │   │   │ ├─ name              │                    ║
║  │ ├─ type             │   │   │ ├─ type              │                    ║
║  │ ├─ location         │   │   │ ├─ location          │                    ║
║  │ ├─ value            │   │   │ ├─ value             │                    ║
║  │ ├─ status           │   │   │ ├─ status            │                    ║
║  │ └─ last_update      │   │   │ └─ last_update       │                    ║
║  │                     │   │   │                      │                    ║
║  │ ALERTS              │   │   │ alerts               │                    ║
║  │ ├─ id (PK)          │   │   │ ├─ id                │                    ║
║  │ ├─ sensor_id (FK)   │   │   │ ├─ sensor_id         │                    ║
║  │ ├─ message          │   │   │ ├─ message           │                    ║
║  │ ├─ level            │   │   │ ├─ level             │                    ║
║  │ ├─ acknowledged     │   │   │ ├─ acknowledged      │                    ║
║  │ └─ timestamp        │   │   │ └─ timestamp         │                    ║
║  │                     │   │   │                      │                    ║
║  │ ACCESS_LOGS         │   │   │ access_logs          │                    ║
║  │ ├─ id (PK)          │   │   │ ├─ id                │                    ║
║  │ ├─ username         │   │   │ ├─ username          │                    ║
║  │ ├─ sensor_id        │   │   │ ├─ sensor_id         │                    ║
║  │ ├─ granted          │   │   │ ├─ granted           │                    ║
║  │ ├─ ip_address       │   │   │ ├─ ip_address        │                    ║
║  │ └─ timestamp        │   │   │ └─ timestamp         │                    ║
║  │                     │   │   │                      │                    ║
║  └─────────────────────┘   │   └──────────────────────┘                    ║
║                             │                                               ║
║  URL: jdbc:h2:mem:starkdb  │   URL: jdbc:postgresql://host:5432/db       ║
║                             │                                               ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 📊 Flujo de Comunicación

### 1. Autenticación
```
Navegador                  Spring Boot                    Base de Datos
  │                           │                              │
  ├─→ POST Login         ────→│                              │
  │   (credentials)           │                              │
  │                           ├─→ Verificar usuario ────────→│
  │                           │← Datos usuario ─────────────│
  │←── Response OK ───────────│                              │
  │   (token saved)           │                              │
```

### 2. Carga de Dashboard
```
Navegador                  Spring Boot                    Base de Datos
  │                           │                              │
  ├─→ GET /api/sensors   ────→│ (HTTP Basic Auth)           │
  │   (with token)            ├─→ Query sensors ──────────→│
  │                           │← Sensor list ──────────────│
  │←── JSON Array ────────────│                              │
  │   (render gráficos)       │                              │
```

### 3. Tiempo Real con WebSocket
```
Navegador                  Spring Boot                    Otros Clientes
  │                           │                              │
  ├─→ Conectar WS        ────→│ STOMP connection           │
  │   (SockJS)                │                              │
  │                           │←── Send alert event ────────┤
  │←── Nuevo evento ──────────│                              │
  │   (actualizar UI)         │                              │
```

---

## 🔄 Sincronización de Datos

### Frontend (Cliente)
```javascript
// Petición cada 30 segundos
setInterval(() => {
    apiCall('/sensors');        // Actualizar sensores
    apiCall('/alerts');         // Actualizar alertas
    apiCall('/access/logs');    // Actualizar logs
}, 30000);

// WebSocket en tiempo real
stompClient.subscribe('/topic/alerts', callback);
stompClient.subscribe('/topic/sensor-data', callback);
```

### Backend (Servidor)
```java
// Envío de eventos vía WebSocket
messagingTemplate.convertAndSend("/topic/alerts", alert);
messagingTemplate.convertAndSend("/topic/sensor-data", sensor);

// Actualizaciones programadas
@Scheduled(fixedDelay = 30000)
public void pollSensorData() { ... }
```

---

## 📁 Estructura de Carpetas

```
StarkDistribuidos/
│
├── src/main/
│   ├── java/com/distribuidos/stark/
│   │   ├── controller/
│   │   │   ├── FrontendController.java         ✨ NUEVO
│   │   │   ├── SensorController.java
│   │   │   ├── AlertController.java
│   │   │   ├── AccessController.java
│   │   │   └── SystemController.java
│   │   │
│   │   ├── service/
│   │   │   ├── SensorService.java
│   │   │   ├── AlertService.java
│   │   │   ├── AccessControlService.java
│   │   │   └── NotificationService.java
│   │   │
│   │   ├── repository/
│   │   │   ├── SensorRepository.java
│   │   │   ├── AlertRepository.java
│   │   │   └── AccessLogRepository.java
│   │   │
│   │   ├── entity/
│   │   │   ├── Sensor.java
│   │   │   ├── Alert.java
│   │   │   └── AccessLog.java
│   │   │
│   │   └── config/
│   │       ├── SecurityConfig.java            ✏️ MODIFICADO
│   │       ├── WebConfig.java                 ✨ NUEVO
│   │       ├── WebSocketConfig.java
│   │       ├── AsyncConfig.java
│   │       └── JpaConfig.java
│   │
│   └── resources/
│       ├── static/                            ✨ NUEVA CARPETA
│       │   ├── index.html                     ✨ NUEVO
│       │   ├── styles/
│       │   │   └── style.css                  ✨ NUEVO
│       │   ├── js/
│       │   │   ├── app.js                     ✨ NUEVO
│       │   │   ├── auth.js                    ✨ NUEVO
│       │   │   ├── dashboard.js               ✨ NUEVO
│       │   │   └── websocket.js               ✨ NUEVO
│       │   └── README.md                      ✨ NUEVO
│       │
│       ├── application.yaml                   ✏️ MODIFICADO
│       ├── data.sql                           ✨ NUEVO
│       └── templates/
│
├── pom.xml
├── docker-compose.yml
├── Dockerfile
│
├── FRONTEND_GUIDE.md                          ✨ NUEVO
├── FRONTEND_QUICKSTART.md                     ✨ NUEVO
├── FRONTEND_SUMMARY.md                        ✨ NUEVO
├── FRONTEND_VERIFICATION.md                   ✨ NUEVO
├── INDEX_FRONTEND.md                          ✨ NUEVO
└── ARCHITECTURE.md                            ✨ ESTE ARCHIVO
```

---

## 🔐 Flujo de Seguridad

```
Usuario
  ↓
[Frontend] Ingresa credenciales
  ↓
Encriptación Base64: btoa('user:password')
  ↓
HTTP Header: Authorization: Basic <encoded>
  ↓
[Backend] Recibe solicitud
  ↓
SecurityFilter: Valida autenticación
  ↓
Spring Security: Verifica usuarios en InMemoryUserDetailsManager
  ↓
Roles verificados: ¿ADMIN? ¿SECURITY? ¿USER?
  ↓
✅ Autorizado → Procesar request
❌ No autorizado → Error 401/403
  ↓
[Frontend] Actualizar localStorage con sesión
  ↓
[Frontend] Redirigir a dashboard si OK
```

---

## 📊 Integración con Microservicios

```
┌─────────────────────────────────────────────────┐
│        FRONTEND (Single Page Application)       │
│                                                  │
│  • Login/Autenticación                          │
│  • Dashboard responsivo                         │
│  • Gráficos interactivos                        │
│  • WebSocket en tiempo real                     │
└─────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────┐
│  API Gateway / Main Application                 │
│  (port 8080 - /stark-security/*)               │
│                                                  │
│  REST API:                                      │
│  • /api/sensors → SensorService                │
│  • /api/alerts → AlertService                  │
│  • /api/access → AccessControlService          │
│  • /api/system → SystemService                 │
│                                                  │
│  WebSocket:                                     │
│  • /ws/notifications → Message Broker          │
└─────────────────────────────────────────────────┘
    ↓           ↓           ↓            ↓
Microservicios (si implementados):
• Sensor Service
• Alert Service
• Access Service
• Notification Service
```

---

## ✨ Ventajas de la Arquitectura

### Frontend
✅ **Sin dependencias pesadas** (Vanilla JS)  
✅ **Rápido cargamiento**  
✅ **Responsive design**  
✅ **Comunicación bidireccional**  

### Backend
✅ **Escalable**  
✅ **Seguro (Basic Auth)**  
✅ **Tiempo real (WebSocket)**  
✅ **Modular (basado en servicios)**  

### Integración
✅ **Separación clara de responsabilidades**  
✅ **Fácil de testear**  
✅ **Fácil de mantener**  
✅ **Fácil de desplegar**  

---

## 🚀 Deployment

### Desarrollo
```bash
mvn spring-boot:run
```

### Producción
```bash
docker build -t stark-security:1.0.0 .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e DB_PASSWORD=secure_password \
  stark-security:1.0.0
```

---

**¡Arquitectura moderna, segura y escalable! ✅**

