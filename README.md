# 🏢 Stark Industries - Sistema de Seguridad Distribuido

## Microservicios con Spring Cloud

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)
![Docker](https://img.shields.io/badge/Docker-Supported-cyan)

---

## 📌 Descripción

Sistema completo de seguridad basado en **microservicios distribuidos**:

- ✅ **Architecture**: 7 microservicios independientes
- ✅ **Service Discovery**: Netflix Eureka (Auto-registro)
- ✅ **API Gateway**: Spring Cloud Gateway (Load balancing)
- ✅ **Autenticación**: JWT + Spring Security
- ✅ **Tiempo Real**: WebSocket + STOMP
- ✅ **Notificaciones**: Email asincrónico
- ✅ **Monitorización**: Spring Actuator
- ✅ **Containerización**: Docker & Docker Compose

---

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Ejecución con Docker (Recomendado)

```bash
# Compilar todos los módulos
mvn clean install

# Iniciar servicios
docker-compose up -d

# Verificar
docker-compose ps
```

**Acceso:**
- API Gateway: http://localhost:8080/stark
- Eureka Dashboard: http://localhost:8761/eureka
- Usuario: admin / admin123

**Credenciales:**
- Usuario: `admin`
- Contraseña: `admin123`

### Opción 2: Docker Compose (Recomendado)

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f stark-app
```

**Servicios disponibles:**
- **App**: http://localhost:8080/stark-security/
- **PostgreSQL**: localhost:5432 (usuario: stark_user)
- **Mailhog**: http://localhost:8025/ (visualizar emails de prueba)
- **pgAdmin**: http://localhost:5050/ (gestionar BD)

---

## 📡 API REST Endpoints

### Sensores
```bash
# Listar sensores
curl -u admin:admin123 http://localhost:8080/stark-security/api/sensors

# Obtener sensor específico
curl -u admin:admin123 http://localhost:8080/stark-security/api/sensors/1

# Crear sensor
curl -u admin:admin123 -X POST http://localhost:8080/stark-security/api/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sensor Movimiento 1",
    "type": "MOTION",
    "location": "Entrada Principal"
  }'

# Actualizar datos del sensor
curl -u admin:admin123 -X PUT \
  http://localhost:8080/stark-security/api/sensors/1/data?value=23.5&status=ACTIVE
```

### Alertas
```bash
# Listar alertas no reconocidas
curl -u admin:admin123 http://localhost:8080/stark-security/api/alerts/unacknowledged

# Reconocer alerta
curl -u admin:admin123 -X PUT \
  http://localhost:8080/stark-security/api/alerts/1/acknowledge

# Obtener estadísticas
curl -u admin:admin123 http://localhost:8080/stark-security/api/alerts/stats
```

### Control de Acceso
```bash
# Registrar intento de acceso
curl -u admin:admin123 -X POST \
  http://localhost:8080/stark-security/api/access/log \
  -d "sensorId=1&username=john&granted=true&ipAddress=192.168.1.100"

# Obtener logs de usuario
curl -u admin:admin123 \
  "http://localhost:8080/stark-security/api/access/logs/john?page=0&size=10"
```

### Sistema
```bash
# Estado del sistema
curl http://localhost:8080/stark-security/api/system/status

# Salud de la aplicación
curl http://localhost:8080/stark-security/api/system/health

# Métricas
curl -u admin:admin123 http://localhost:8080/stark-security/api/system/metrics
```

---

## 🧵 Conexión WebSocket

### Cliente JavaScript

```javascript
const socket = new SockJS('/stark-security/ws/notifications');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Conectado al WebSocket');
    
    // Suscribirse a alertas
    stompClient.subscribe('/topic/alerts', function(message) {
        const alert = JSON.parse(message.body);
        console.log('Nueva alerta:', alert);
    });
    
    // Suscribirse a datos de sensores
    stompClient.subscribe('/topic/sensor-data', function(message) {
        const sensor = JSON.parse(message.body);
        console.log('Datos sensor:', sensor);
    });
});
```

---

## 🔐 Seguridad

### Usuarios de Desarrollo

| Usuario | Contraseña | Roles |
|---------|-----------|-------|
| admin | admin123 | ADMIN, SECURITY, USER |
| security | security123 | SECURITY, USER |
| user | user123 | USER |

### Autenticación

Todos los endpoints REST requieren HTTP Basic Auth:

```bash
curl -u admin:admin123 http://localhost:8080/stark-security/api/sensors
```

### Autorización (Roles)

```
ADMIN      → Acceso total al sistema
SECURITY   → Gestión sensores, alertas y acceso
USER       → Acceso a información propia
```

---

## 📁 Estructura del Proyecto

```
StarkDistribuidos/
├── src/main/java/com/distribuidos/stark/
│   ├── entity/              # Entidades JPA (Sensor, Alert, AccessLog)
│   ├── repository/          # Repositorios Spring Data
│   ├── service/             # Servicios con lógica de negocio
│   ├── controller/          # Controladores REST
│   ├── config/              # Configuraciones (Security, Async, WebSocket)
│   └── StarkDistribuidosApplication.java
├── src/main/resources/
│   ├── application.yaml     # Configuración multi-perfil
│   ├── static/              # Archivos estáticos (CSS, JS)
│   └── templates/           # Vistas Thymeleaf
├── Dockerfile               # Imagen Docker
├── docker-compose.yml       # Orquestación de servicios
├── pom.xml                  # Dependencias Maven
└── README.md
```

---

## 🐳 Docker

### Construir imagen

```bash
docker build -t stark-security:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DB_PASSWORD=stark_secure_password \
  stark-security:1.0.0
```

### Docker Compose completo

```bash
# Iniciar
docker-compose up -d

# Detener
docker-compose down

# Ver logs
docker-compose logs -f stark-app
```

---

## 📊 Funcionalidades Principales

### ✅ Gestión de Sensores
- Crear, leer, actualizar, eliminar sensores
- Tipos: MOTION, TEMPERATURE, ACCESS
- Activar/desactivar sensores
- Monitorizar fallos

### ✅ Sistema de Alertas
- Generación automática según umbrales
- Niveles: CRITICAL, WARNING, INFO
- Reconocimiento de alertas
- Email de alertas críticas

### ✅ Control de Acceso
- Registro de intentos de acceso
- Seguimiento de accesos denegados
- Auditoría de usuarios
- Reportes de seguridad

### ✅ Comunicación en Tiempo Real
- WebSocket STOMP
- Notificaciones instantáneas
- Múltiples canales
- SockJS para compatibilidad

### ✅ Asincronía
- Procesamiento paralelo de sensores
- Envío de alertas sin bloqueos
- Procesamiento batch de datos
- 3 ejecutores optimizados

---

## 🔧 Configuración

### Perfiles de Spring

```bash
# Desarrollo (H2)
mvn spring-boot:run

# Producción (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=production"

# Docker
docker-compose up
```

### Variables de Entorno

```bash
# Base de datos
DB_PASSWORD=tu_password
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/dbname

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password

# Sistema
SERVER_PORT=8080
LOGGING_LEVEL=INFO
```

---

## 📈 Monitorización

### Actuator Endpoints

```bash
# Health
curl http://localhost:8080/stark-security/actuator/health

# Métricas
curl http://localhost:8080/stark-security/actuator/metrics

# Info
curl http://localhost:8080/stark-security/actuator/info

# Thread dump
curl http://localhost:8080/stark-security/actuator/threaddump
```

### Logging

Logs configurados en `application.yaml`:
- **ROOT**: INFO
- **com.distribuidos.stark**: DEBUG (desarrollo) / INFO (producción)

---

## 📚 Documentación Adicional

- [SETUP.md](./SETUP.md) - Guía de instalación
- [CONFIGURATION.md](./CONFIGURATION.md) - Configuración técnica
- [QUICKSTART.md](./QUICKSTART.md) - Inicio rápido
- [CODE_EXAMPLES.md](./CODE_EXAMPLES.md) - Ejemplos de código
- [IMPLEMENTACION_COMPLETA.md](./IMPLEMENTACION_COMPLETA.md) - Detalles completos

---

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Tests con cobertura
mvn test jacoco:report

# Verificar compilación
mvn clean compile
```

---

## 🎯 Dependencias Principales

```xml
<!-- Spring Boot -->
spring-boot-starter-webmvc      (REST API)
spring-boot-starter-security    (Autenticación/Autorización)
spring-boot-starter-data-jpa    (Persistencia)
spring-boot-starter-websocket   (Notificaciones en tiempo real)
spring-boot-starter-mail        (Emails)

<!-- Base de Datos -->
h2                              (Desarrollo)
postgresql                      (Producción)

<!-- Herramientas -->
lombok                          (Reducir boilerplate)
spring-boot-starter-validation  (Validación de datos)
spring-boot-starter-actuator    (Monitorización)
```

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## ✉️ Contacto

**Stark Industries Security Team**  
Email: security@starkindustries.com  
Website: https://www.starkindustries.com

---

## 🙏 Agradecimientos

- Spring Boot Team
- PostgreSQL Community
- Docker Community

---

**¡Gracias por usar Stark Industries Security System! 🚀**

---

*Última actualización: 2026-03-17*  
*Versión: 1.0.0*  
*Estado: Producción Ready ✅*

