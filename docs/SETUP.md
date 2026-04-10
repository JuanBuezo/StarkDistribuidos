# 🏢 Stark Industries - Sistema de Seguridad Distribuido

## 📋 Descripción

Sistema de seguridad concurrente para Stark Industries que gestiona:
- ✅ Múltiples sensores en tiempo real
- ✅ Control de acceso basado en roles
- ✅ Notificaciones instantáneas via WebSocket
- ✅ Alertas por email
- ✅ Monitorización en tiempo real

## 🛠️ Configuración Realizada

### 1. **Dependencias Maven**
Se han agregado todas las dependencias necesarias:
- `spring-boot-starter-webmvc` - API REST
- `spring-boot-starter-security` - Autenticación y autorización
- `spring-boot-starter-data-jpa` - Persistencia de datos
- `spring-boot-starter-websocket` - Notificaciones en tiempo real
- `spring-boot-starter-mail` - Envío de emails
- `spring-boot-starter-actuator` - Monitorización
- `spring-boot-starter-validation` - Validación de datos
- `h2` - Base de datos para desarrollo

### 2. **Configuraciones Spring**

#### SecurityConfig.java
- ✅ Autenticación básica HTTP
- ✅ Control de acceso basado en roles (ADMIN, SECURITY, USER)
- ✅ Encriptación BCrypt
- ✅ Usuarios de desarrollo precargados
- ✅ Rutas protegidas por rol

#### AsyncConfig.java
- ✅ Pool de hilos para tareas asincrónicas
- ✅ Executor para procesamiento de sensores
- ✅ Executor para notificaciones y alertas
- ✅ Soporte para `@Async` en servicios

#### WebSocketConfig.java
- ✅ Broker de mensajes STOMP
- ✅ Endpoints para notificaciones, sensores y alertas
- ✅ SockJS para navegadores sin WebSocket
- ✅ CORS habilitado

#### JpaConfig.java
- ✅ Auditoría automática de entidades
- ✅ Escaneo de repositorios Spring Data

### 3. **Aplicación Principal**
`StarkDistribuidosApplication.java` habilitada con:
- `@EnableAsync` - Soporte para métodos asincronos
- `@EnableScheduling` - Soporte para tareas programadas

### 4. **Propiedades (application.yaml)**
Configuradas:
- Base de datos H2 en memoria
- JPA/Hibernate con DDL automático
- Logging en DEBUG para el paquete `com.distribuidos.stark`
- Endpoints de Actuator para monitorización
- WebSocket con CORS habilitado
- Servidor en puerto 8080 con contexto `/stark-security`

---

## 🔐 Usuarios Predefinidos (Desarrollo)

| Usuario | Contraseña | Roles |
|---------|-----------|-------|
| `admin` | `admin123` | ADMIN, SECURITY, USER |
| `security` | `security123` | SECURITY, USER |
| `user` | `user123` | USER |

---

## 🚀 Cómo Ejecutar

### 1. Compilar el proyecto
```bash
mvnw clean compile
```

### 2. Ejecutar la aplicación
```bash
mvnw spring-boot:run
```

### 3. Acceder a la aplicación
- **API REST**: http://localhost:8080/stark-security/
- **H2 Console**: http://localhost:8080/stark-security/h2-console
- **WebSocket**: ws://localhost:8080/stark-security/ws/notifications
- **Actuator**: http://localhost:8080/stark-security/actuator

### 4. Monitorización
```bash
# Ver métricas
http://localhost:8080/stark-security/actuator/metrics

# Ver salud del sistema
http://localhost:8080/stark-security/actuator/health

# Ver estado de threads
http://localhost:8080/stark-security/actuator/threaddump
```

---

## 📝 Rutas Protegidas

### Rutas Públicas
- `GET /stark-security/public/**` - Acceso público

### Rutas de Sensores
- `GET/POST /stark-security/sensors/**` - Requiere rol ADMIN o SECURITY

### Rutas de Alertas
- `GET/POST /stark-security/alerts/**` - Requiere rol ADMIN o SECURITY

### Rutas de Acceso
- `GET/POST /stark-security/access/**` - Requiere rol ADMIN, SECURITY o USER

---

## 🔄 Próximos Pasos

Para continuar con la implementación:

1. **Crear Entidades JPA**
   - `Sensor` - Representa cada sensor del sistema
   - `Alert` - Registra alertas generadas
   - `AccessLog` - Logs de acceso
   - `User` - Usuarios del sistema

2. **Crear Repositorios Spring Data**
   - `SensorRepository`
   - `AlertRepository`
   - `AccessLogRepository`
   - `UserRepository`

3. **Crear Servicios**
   - `SensorService` - Gestión de sensores
   - `AlertService` - Generación y notificación de alertas
   - `AccessControlService` - Control de acceso
   - `NotificationService` - Envío de notificaciones

4. **Crear Controladores REST**
   - `SensorController`
   - `AlertController`
   - `AccessController`

5. **Implementar WebSocket Handlers**
   - Notificaciones en tiempo real
   - Actualizaciones de estado de sensores

---

## 📚 Referencia de Documentación

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [Maven Documentation](https://maven.apache.org/documentation.html)

---

## ✅ Estado del Proyecto

- ✅ Dependencias agregadas
- ✅ Configuración de seguridad
- ✅ Configuración de concurrencia (Async)
- ✅ Configuración de WebSocket
- ✅ Configuración de JPA
- ✅ Base de datos H2
- ⏳ Entidades (próximo)
- ⏳ Repositorios (próximo)
- ⏳ Servicios (próximo)
- ⏳ Controladores REST (próximo)

---

**Desarrollado para Sistemas Distribuidos - Universidad Pontificia Comillas**

