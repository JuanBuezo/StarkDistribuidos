# Documentación de Configuración - Stark Industries

## 📁 Estructura del Proyecto

```
StarkDistribuidos/
├── src/
│   ├── main/
│   │   ├── java/com/distribuidos/stark/
│   │   │   ├── StarkDistribuidosApplication.java    (✅ Configurado)
│   │   │   ├── ServletInitializer.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java              (✅ Nueva)
│   │   │   │   ├── AsyncConfig.java                 (✅ Nueva)
│   │   │   │   ├── WebSocketConfig.java             (✅ Nueva)
│   │   │   │   └── JpaConfig.java                   (✅ Nueva)
│   │   │   ├── model/                               (⏳ Por crear)
│   │   │   ├── entity/                              (⏳ Por crear)
│   │   │   ├── repository/                          (⏳ Por crear)
│   │   │   ├── service/                             (⏳ Por crear)
│   │   │   ├── controller/                          (⏳ Por crear)
│   │   │   └── websocket/                           (⏳ Por crear)
│   │   └── resources/
│   │       ├── application.yaml                     (✅ Configurado)
│   │       ├── static/                              (Para frontend)
│   │       └── templates/                           (Para Thymeleaf)
│   └── test/                                        (Tests unitarios)
├── pom.xml                                          (✅ Actualizado)
└── README.md
```

---

## 🔧 Configuraciones Implementadas

### 1. **pom.xml** ✅
Se agregaron todas las dependencias esenciales:
- Dependencias de Spring Boot
- Spring Security
- Spring Data JPA
- Spring WebSocket
- Spring Mail
- Lombok
- H2 Database

### 2. **application.yaml** ✅
Configuración completa:
```yaml
spring:
  datasource: H2 en memoria
  jpa: Hibernate configurado
  mail: Cliente SMTP preparado
  websocket: Brokers configurados
management:
  endpoints: Actuator habilitado
```

### 3. **SecurityConfig.java** ✅
- Autenticación HTTP Basic
- 3 roles: ADMIN, SECURITY, USER
- Control de acceso por rutas
- Encriptación BCrypt

### 4. **AsyncConfig.java** ✅
- 3 ejecutores de hilos configurados:
  1. `taskExecutor` (5-10 hilos) - Tareas generales
  2. `sensorExecutor` (8-16 hilos) - Procesamiento de sensores
  3. `alertExecutor` (4-8 hilos) - Notificaciones

### 5. **WebSocketConfig.java** ✅
- 3 endpoints STOMP:
  - `/ws/notifications` - Notificaciones
  - `/ws/sensors` - Estado de sensores
  - `/ws/alerts` - Alertas en tiempo real
- SockJS para compatibilidad
- CORS habilitado

### 6. **JpaConfig.java** ✅
- Auditoría automática
- Repositorios de Spring Data JPA

---

## 🔐 Control de Acceso

### Rutas Protegidas

```
┌─────────────────────────────────────────────────┐
│         CONTROL DE ACCESO POR ROL                │
├─────────────────────────────────────────────────┤
│ RUTA                    │ ROLES PERMITIDOS        │
├─────────────────────────┼───────────────────────┤
│ /public/**              │ Público                │
│ /sensors/**             │ ADMIN, SECURITY        │
│ /alerts/**              │ ADMIN, SECURITY        │
│ /access/**              │ ADMIN, SECURITY, USER  │
│ Cualquier otra          │ Autenticado            │
└─────────────────────────────────────────────────┘
```

---

## 🧵 Concurrencia

### Pool de Hilos Configurado

```
┌────────────────────────────────────────┐
│        CONFIGURACIÓN DE EJECUTORES      │
├────────────────────────────────────────┤
│ Nombre        │ Core │ Max │ Queue     │
├───────────────┼──────┼─────┼───────────┤
│ taskExecutor  │  5   │ 10  │   100     │
│ sensorExecutor│  8   │ 16  │   200     │
│ alertExecutor │  4   │ 8   │   100     │
└────────────────────────────────────────┘
```

**Uso:**
```java
@Service
public class MiServicio {
    
    // Usa el executor por defecto (taskExecutor)
    @Async
    public void tareaAsincrona() {
        // Se ejecuta en un hilo diferente
    }
    
    // Usa executor específico de sensores
    @Async("sensorExecutor")
    public void procesarSensor() {
        // Se ejecuta en sensorExecutor
    }
    
    // Usa executor de alertas
    @Async("alertExecutor")
    public void enviarAlerta() {
        // Se ejecuta en alertExecutor
    }
}
```

---

## 📡 WebSocket - Canales de Comunicación

### Endpoints Disponibles

```
ws://localhost:8080/stark-security/ws/notifications
├─ /topic/alerts      → Alertas generales
├─ /topic/updates     → Actualizaciones del sistema
└─ /queue/personal    → Mensajes personales

ws://localhost:8080/stark-security/ws/sensors
├─ /topic/sensor-data → Datos de todos los sensores
├─ /topic/sensor/{id} → Datos de un sensor específico
└─ /queue/status      → Estado de conexión

ws://localhost:8080/stark-security/ws/alerts
├─ /topic/critical    → Alertas críticas
├─ /topic/warning     → Advertencias
└─ /queue/acknowledge → Confirmación de alertas
```

### Cliente JavaScript de Ejemplo

```javascript
// Conectar al WebSocket
const socket = new SockJS('/stark-security/ws/notifications');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Conectado: ' + frame.version);
    
    // Suscribirse a alertas
    stompClient.subscribe('/topic/alerts', function(message) {
        console.log('Alerta recibida:', message.body);
    });
});
```

---

## 🗄️ Base de Datos H2

### Consola H2
```
URL: http://localhost:8080/stark-security/h2-console
Controlador: org.h2.Driver
URL JDBC: jdbc:h2:mem:starkdb
Usuario: sa
Contraseña: (dejar en blanco)
```

### Base de Datos en Memoria
- ✅ Se crea al iniciar
- ✅ Se destruye al detener
- ✅ Ideal para desarrollo y pruebas

---

## 📊 Monitorización (Actuator)

### Endpoints Habilitados

```
http://localhost:8080/stark-security/actuator/
├─ /health          → Estado de salud del sistema
├─ /metrics         → Métricas de rendimiento
├─ /info            → Información de la aplicación
└─ /threaddump      → Estado de todos los threads
```

### Ejemplo de Consulta

```bash
# Ver salud
curl http://localhost:8080/stark-security/actuator/health

# Ver métricas
curl http://localhost:8080/stark-security/actuator/metrics

# Ver threads
curl http://localhost:8080/stark-security/actuator/threaddump
```

---

## 🚀 Próximas Fases de Desarrollo

### Fase 2: Entidades y Repositorios
- [ ] Crear entidades JPA (Sensor, Alert, AccessLog, User)
- [ ] Implementar repositorios Spring Data
- [ ] Configurar auditoría de entidades

### Fase 3: Servicios
- [ ] SensorService - Gestión de sensores
- [ ] AlertService - Generación de alertas
- [ ] AccessControlService - Control de acceso
- [ ] NotificationService - Notificaciones

### Fase 4: Controladores
- [ ] SensorController - API REST para sensores
- [ ] AlertController - API REST para alertas
- [ ] AccessController - API REST para acceso

### Fase 5: WebSocket
- [ ] StompMessageHandler - Manejo de mensajes
- [ ] NotificationPublisher - Publicación de notificaciones
- [ ] SensorDataPublisher - Publicación de datos de sensores

### Fase 6: Testing
- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Tests de carga

---

## 📝 Notas Importantes

1. **H2 Database**: La base de datos se reinicia cada vez que se inicia la aplicación
2. **Logging**: En DEBUG para depuración, cambiar a INFO en producción
3. **WebSocket**: Requiere compatibilidad con navegadores modernos
4. **Seguridad**: Los usuarios hardcodeados son solo para desarrollo
5. **Email**: Configurado para un servidor local (cambiar para producción)

---

## 🔗 Enlaces Útiles

- [Spring Boot Reference](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [STOMP Protocol](https://stomp.github.io/)
- [H2 Database Documentation](https://www.h2database.com/)

---

**Estado: ✅ CONFIGURACIÓN COMPLETADA**
**Próximo paso: Crear entidades JPA**

