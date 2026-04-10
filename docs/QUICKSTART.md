# 🚀 Quick Start Guide - Stark Industries

## Inicio Rápido (30 segundos)

### 1. Compilar
```bash
mvnw clean compile
```

### 2. Ejecutar
```bash
mvnw spring-boot:run
```

### 3. Acceder
```
http://localhost:8080/stark-security/
```

---

## 🔑 Credenciales de Prueba

```
Usuario: admin
Contraseña: admin123
Roles: ADMIN, SECURITY, USER
```

O para cURL:
```bash
curl -u admin:admin123 http://localhost:8080/stark-security/sensors/
```

---

## 📍 Endpoints Útiles

| Endpoint | Descripción | Auth |
|----------|-------------|------|
| `/actuator/health` | Salud del sistema | ❌ |
| `/h2-console` | Base de datos | ❌ |
| `/sensors/` | Gestión de sensores | ✅ ADMIN/SECURITY |
| `/alerts/` | Gestión de alertas | ✅ ADMIN/SECURITY |
| `/access/` | Control de acceso | ✅ ADMIN/SECURITY/USER |

---

## 🧡 Usuarios Disponibles

| Usuario | Pass | Roles |
|---------|------|-------|
| admin | admin123 | ADMIN, SECURITY, USER |
| security | security123 | SECURITY, USER |
| user | user123 | USER |

---

## 🔧 Configuraciones Principales

### SecurityConfig.java
- Autenticación HTTP Basic
- Control por roles
- Encriptación BCrypt

### AsyncConfig.java
- taskExecutor (5-10 hilos)
- sensorExecutor (8-16 hilos)
- alertExecutor (4-8 hilos)

### WebSocketConfig.java
- /ws/notifications
- /ws/sensors
- /ws/alerts

### application.yaml
- H2 Database
- JPA/Hibernate
- Logging DEBUG
- Actuator enabled
- Puerto: 8080
- Contexto: /stark-security

---

## 🗄️ Base de Datos H2

```
URL: http://localhost:8080/stark-security/h2-console
Driver: org.h2.Driver
JDBC URL: jdbc:h2:mem:starkdb
Usuario: sa
Contraseña: (vacía)
```

---

## 📊 Estado de la App

```bash
# Salud
curl http://localhost:8080/stark-security/actuator/health

# Métricas
curl http://localhost:8080/stark-security/actuator/metrics

# Threads
curl http://localhost:8080/stark-security/actuator/threaddump
```

---

## 🧪 Tests

```bash
# Ejecutar todos los tests
mvnw test

# Resultado esperado: 10/10 PASSED ✅
```

---

## 📁 Carpetas Clave

```
src/main/java/com/distribuidos/stark/
├── config/              → Configuraciones Spring ✅
├── entity/              → Entidades JPA (próximo)
├── repository/          → Repositorios (próximo)
├── service/             → Servicios (próximo)
└── controller/          → Controladores (próximo)

src/main/resources/
├── application.yaml     → Propiedades ✅
├── static/              → Archivos estáticos
└── templates/           → Vistas Thymeleaf
```

---

## 💡 Próximos Pasos

1. Crear entidades JPA (Sensor, Alert, etc.)
2. Crear repositorios Spring Data
3. Crear servicios con @Async
4. Crear controladores REST
5. Implementar WebSocket handlers
6. Escribir tests

---

## 🔄 Métodos Útiles

### Usar @Async
```java
@Service
public class MiServicio {
    
    @Async
    public void tareaAsincrona() {
        // Se ejecuta en un hilo diferente
    }
    
    @Async("sensorExecutor")
    public void procesarSensor() {
        // Se ejecuta en sensorExecutor
    }
}
```

### Usar WebSocket
```javascript
const socket = new SockJS('/stark-security/ws/notifications');
const stompClient = Stomp.over(socket);
stompClient.connect({}, function() {
    stompClient.subscribe('/topic/alerts', (msg) => {
        console.log('Alerta:', msg.body);
    });
});
```

---

## 🐛 Troubleshooting

### El puerto 8080 está en uso
```bash
# Cambiar en application.yaml
server:
  port: 8081
```

### Limpiar caché Maven
```bash
mvnw clean install
```

### Problemas con H2
```bash
# Reinicia la app, H2 se crea automáticamente
mvnw clean test
```

---

## 📚 Documentación

- **SETUP.md** - Guía completa de configuración
- **CONFIGURATION.md** - Detalles técnicos de cada config
- **pom.xml** - Todas las dependencias

---

## ✅ Status

- ✅ Dependencias configuradas
- ✅ Seguridad funcionando
- ✅ Async habilitado
- ✅ WebSocket configurado
- ✅ JPA/Hibernate funcionando
- ✅ Tests pasando (10/10)
- ⏳ Entidades por crear
- ⏳ Servicios por implementar
- ⏳ Controladores por crear

---

**¡Listo para empezar! 🚀**

Próximo paso: Crear entidades JPA

