# ⚡ Quick Start - Frontend Microservice

## 📁 Ubicación del Microservicio

```
StarkDistribuidos/
└── starkDistribuidos-frontend/    ← NUEVO MICROSERVICIO
    ├── pom.xml
    ├── README.md
    └── src/
        ├── main/java/...          (Controllers + Config)
        └── main/resources/static/ (HTML/CSS/JS)
```

---

## 🚀 Ejecución Rápida

### Opción 1: Desde el Microservicio
```bash
cd starkDistribuidos-frontend
mvn spring-boot:run
```

### Opción 2: Desde la Raíz
```bash
mvn spring-boot:run -pl :stark-frontend
```

### Opción 3: Compilar Primero
```bash
mvn clean install -DskipTests
cd starkDistribuidos-frontend
mvn spring-boot:run
```

---

## 🌐 Acceso

```
URL:          http://localhost:8085/stark-security/
Puerto:       8085
Usuario:      admin
Contraseña:   admin123
```

---

## 📊 Estructura del Microservicio

### Java
```
src/main/java/com/distribuidos/stark/
├── StarkFrontendApplication.java       (Spring Boot App)
├── controller/
│   └── FrontendController.java         (Rutas HTML)
└── config/
    ├── SecurityConfig.java             (Autenticación)
    └── WebConfig.java                  (Recursos)
```

### Frontend
```
src/main/resources/static/
├── index.html                    (Dashboard)
├── styles/style.css              (1000+ líneas)
└── js/
    ├── app.js                    (Utilidades)
    ├── auth.js                   (Login)
    ├── dashboard.js              (Dashboard)
    └── websocket.js              (Tiempo real)
```

### Config
```
src/main/resources/
└── application.yaml              (Desarrollo + Producción)
```

---

## 📦 Maven

### Compilación
```bash
# Solo el frontend
mvn clean compile -pl :stark-frontend

# Todo el proyecto
mvn clean install -DskipTests
```

### POM.xml
```xml
<!-- Configuración básica -->
<artifactId>stark-frontend</artifactId>
<name>Stark Frontend Service</name>

<!-- Dependencias incluidas -->
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-websocket
- spring-cloud-starter-eureka-client
- h2, postgresql, lombok, testing
```

---

## ✨ Características

| Característica | Estado |
|---|---|
| Autenticación | ✅ HTTP Basic Auth |
| Dashboard | ✅ 5 pestañas |
| WebSocket | ✅ Tiempo real |
| Responsive | ✅ Mobile → Desktop |
| Eureka | ✅ Integrado |
| Seguridad | ✅ Roles y permisos |

---

## 🔐 Credenciales

```
admin    / admin123    (ADMIN, SECURITY, USER)
security / security123 (SECURITY, USER)
user     / user123     (USER)
```

---

## 📡 Comunicación

### APIs Externas
```
GET    /api/sensors          (Service 8082)
GET    /api/alerts           (Service 8083)
GET    /api/access/logs      (Service 8081)
WS     /ws/notifications     (WebSocket)
```

### URLs Públicas
```
GET /stark-security/         (Sin auth)
GET /stark-security/static/**
WS  /stark-security/ws/**
```

---

## 🎯 Dashboard

### Pestañas
1. 📊 **Overview** - Estadísticas y gráficos
2. 📡 **Sensores** - Gestión de sensores
3. 🚨 **Alertas** - Sistema de alertas
4. 🔐 **Acceso** - Logs de auditoría

### Funcionalidades
- Gráficos en tiempo real
- Feed de eventos en vivo
- Actualización cada 30 segundos
- WebSocket automático

---

## 🐳 Docker

### Build
```bash
cd starkDistribuidos-frontend
docker build -t stark-frontend:1.0.0 .
```

### Run
```bash
docker run -p 8085:8085 \
  -e EUREKA_SERVER=http://eureka:8761/eureka/ \
  stark-frontend:1.0.0
```

---

## 📚 Documentación

- **README.md** (en la carpeta del servicio)
- **FRONTEND_MICROSERVICE_SETUP.md** (en la raíz)
- **FRONTEND_GUIDE.md** (guía completa)
- **ARCHITECTURE.md** (diagrama)

---

## ✅ Compilación Verificada

```
✅ BUILD SUCCESS
  Stark Frontend Service ......... SUCCESS
  (9/9 módulos compilados)
```

---

## 🎉 Status

```
✅ Microservicio Independiente
✅ Integrado con Eureka
✅ Compatible con otros servicios
✅ Listo para Producción
```

---

**¡El frontend es ahora un microservicio completo!** ⚡

*Puerto: 8085*  
*Context: /stark-security/*  
*Versión: 1.0.0*

