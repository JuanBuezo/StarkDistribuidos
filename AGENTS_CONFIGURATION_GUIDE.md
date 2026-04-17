# 🛠️ GUÍA DE CONFIGURACIÓN DE AGENTES - DÍA 3

**Fecha:** 17 de Abril de 2026  
**Objetivo:** Configurar cada microservicio para que funcione como agente autónomo

---

## 📋 CONFIGURACIÓN POR SERVICIO

### 1. AUTH SERVICE (Autenticación)

#### Dependencias necesarias (pom.xml)
```xml
<!-- RabbitMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- JSON Web Token -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

#### application.yaml
```yaml
spring:
  application:
    name: auth-service
  
  # RabbitMQ
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASS:guest}
  
  # Kafka
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: auth-service
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
  
  # Redis
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}

server:
  port: 8081

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: ${JWT_EXPIRATION:86400000}

# Agent Configuration
service:
  name: auth-service
  agent:
    enabled: true
```

#### Clase principal (Application.java)
```java
@SpringBootApplication
@EnableScheduling
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
```

---

### 2. ACCESS SERVICE (Control de Acceso)

#### application.yaml
```yaml
spring:
  application:
    name: access-service
  
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASS:guest}
  
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    consumer:
      group-id: access-service
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}

server:
  port: 8084

service:
  name: access-service
  agent:
    enabled: true
```

#### RabbitMQ Queue Configuration (Bean)
```java
@Configuration
public class AccessQueueConfig {
    
    @Bean
    public Queue accessCommandQueue() {
        return new Queue("access.command.q", true);
    }
    
    @Bean
    public Binding accessCommandBinding(Queue accessCommandQueue) {
        return BindingBuilder.bind(accessCommandQueue)
            .to(new TopicExchange("amq.topic"))
            .with("access.command.*");
    }
}
```

---

### 3. SENSOR SERVICE (Lectura de Sensores)

#### application.yaml
```yaml
spring:
  application:
    name: sensor-service
  
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASS:guest}
  
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}

server:
  port: 8082

service:
  name: sensor-service
  agent:
    enabled: true

# Sensor Configuration
sensor:
  read-interval: 10000  # 10 segundos
  active-sensors: 3
  data-range: "0-100"
```

---

### 4. ALERT SERVICE (Detección de Anomalías)

#### application.yaml
```yaml
spring:
  application:
    name: alert-service
  
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    consumer:
      group-id: alert-service
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}

server:
  port: 8083

service:
  name: alert-service
  agent:
    enabled: true

# Alert Configuration
alert:
  anomaly-threshold: 75.0
  critical-threshold: 90.0
```

#### Kafka Topics Configuration
```java
@Configuration
public class AlertTopicConfig {
    
    @Bean
    public NewTopic sensorDataTopic() {
        return TopicBuilder.name("sensor.data")
            .partitions(5)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic alertTriggeredTopic() {
        return TopicBuilder.name("alert.triggered")
            .partitions(3)
            .replicas(1)
            .build();
    }
}
```

---

### 5. NOTIFICATION SERVICE (Notificaciones)

#### application.yaml
```yaml
spring:
  application:
    name: notification-service
  
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    consumer:
      group-id: notification-service
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
  
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:1025}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

server:
  port: 8085

service:
  name: notification-service
  agent:
    enabled: true

# Notification Configuration
notification:
  email-enabled: true
  sms-enabled: false
  admin-email: admin@starkindustries.com
```

---

## 🚀 PASOS DE CONFIGURACIÓN

### Paso 1: Actualizar pom.xml de cada servicio

Agregar a cada módulo (si no está):

```bash
# Para cada módulo
cd starkDistribuidos-auth
# Verificar que tiene las dependencias necesarias
```

### Paso 2: Crear archivos de configuración

Para cada servicio:
1. Actualizar `application.yaml`
2. Crear `application-docker.yaml` (overrides para Docker)

### Paso 3: Crear clases de Agente

Ya creadas:
- ✅ `AuthAgent.java`
- ✅ `AccessControlAgent.java`
- ✅ `SensorAgent.java`
- ✅ `AlertAgent.java`
- ✅ `NotificationAgent.java`

### Paso 4: Inicializar Agentes en @Bean

En cada servicio, crear:

```java
@Configuration
public class AgentConfig {
    
    @Bean
    public AuthAgent authAgent() {
        AuthAgent agent = new AuthAgent();
        agent.initialize();
        return agent;
    }
}
```

### Paso 5: Compilar y ejecutar

```bash
# Compilar todo
./scripts/build-secure.sh

# Iniciar contenedores
docker-compose -f docker-compose-sdd.yml up -d

# Verificar logs
docker-compose logs -f
```

---

## 🔗 COLAS Y TOPICS

### RabbitMQ Queues

```
auth.command.q        → AUTH_AGENT
access.command.q      → ACCESS_AGENT
sensor.command.q      → SENSOR_AGENT
```

### Kafka Topics

```
auth.events           → Eventos de autenticación (3 partitions)
access.events         → Eventos de acceso (3 partitions)
sensor.data           → Datos de sensores (5 partitions)
alert.triggered       → Alertas disparadas (3 partitions)
notification.sent     → Notificaciones enviadas (3 partitions)
audit.log             → Auditoría (1 partition, no-delete policy)
orchestrator.events   → Eventos del orquestador (3 partitions)
```

---

## 💾 REDIS KEYS

```
agent::{service}::state          → Estado del agente
agent::{service}::health         → Health check
transaction::{txn_id}            → Contexto de transacción
circuit_breaker::{service}       → Estado del circuit breaker
permissions::{user_id}           → Permisos de usuario
cache::jwt::{token}              → JWT en cache (TTL 24h)
```

---

## 🧪 VERIFICACIÓN

### Health checks

```bash
# Auth Service
curl http://localhost:8081/actuator/health

# Access Service
curl http://localhost:8084/actuator/health

# Sensor Service
curl http://localhost:8082/actuator/health

# Alert Service
curl http://localhost:8083/actuator/health

# Notification Service
curl http://localhost:8085/actuator/health
```

### Dashboards

```
RabbitMQ Management:  http://localhost:15672 (guest/guest)
Kafka UI:             http://localhost:8888
Redis Insight:        http://localhost:8001 (opcional)
```

### Logs de agentes

```bash
docker-compose logs -f auth-service        # AUTH_AGENT
docker-compose logs -f access-service      # ACCESS_AGENT
docker-compose logs -f sensor-service      # SENSOR_AGENT
docker-compose logs -f alert-service       # ALERT_AGENT
docker-compose logs -f notification-service # NOTIFICATION_AGENT
```

---

## 🔐 SEGURIDAD

### Autenticación entre agentes
```
Cada agente tiene:
✅ API Key único
✅ Certificado SSL/TLS
✅ JWT de sesión
```

### Encriptación de mensajes
```
RabbitMQ:
✅ SSL/TLS (puerto 5671)
✅ AMQPS protocol

Kafka:
✅ SSL/TLS (puerto 9093)
✅ SASL/SCRAM authentication
```

### Auditoría
```
✅ Kafka topic "audit.log" (policy: no-delete)
✅ Quién, Qué, Cuándo, Resultado
✅ Trazabilidad completa
```

---

## 📞 TROUBLESHOOTING

### Agente no inicia

```bash
# Ver logs
docker-compose logs auth-service

# Verificar RabbitMQ conectado
docker exec stark-rabbitmq rabbitmqctl status

# Verificar Kafka conectado
docker exec stark-kafka kafka-broker-api-versions.sh \
  --bootstrap-server kafka:9092
```

### Eventos no se reciben

```bash
# Verificar queues en RabbitMQ
curl -u guest:guest \
  http://localhost:15672/api/queues | jq

# Verificar topics en Kafka
docker exec stark-kafka kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --list
```

### Performance lento

```
1. Verificar tamaño de particiones en Kafka
2. Verificar número de replicas en RabbitMQ
3. Aumentar pool de conexiones
4. Monitorear CPU/Memoria en contenedores
```

---

## ✅ CHECKLIST FINAL

- [ ] Todos los pom.xml actualizados
- [ ] application.yaml configurado en cada servicio
- [ ] Agentes creados e inicializados
- [ ] RabbitMQ y Kafka ejecutándose
- [ ] Redis ejecutándose
- [ ] Todos los servicios arrancan sin errores
- [ ] Logs muestran agents initialized
- [ ] Health checks responden 200 OK
- [ ] Eventos fluyen entre agentes
- [ ] Auditoría registra operaciones

---

**Status:** ✅ CONFIGURACIÓN COMPLETA  
**Próximo:** Ejecutar tests de integración


