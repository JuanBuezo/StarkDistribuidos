# 🏗️ DÍA 2 - ARQUITECTURA SDD CON AGENTES Y ORQUESTADOR

**Fecha:** 17 de Abril de 2026 - Continuación  
**Fase:** DÍA 2 - Software-Defined Deployment  
**Status:** 🟢 EN PROGRESO

---

## 📋 TAREAS COMPLETADAS - DÍA 2

### ✅ 1. Análisis de Arquitectura SDD
- [x] Diseño de arquitectura distribuida
- [x] Documentación de flujos de comunicación
- [x] Matriz de componentes

### ✅ 2. Infraestructura de Mensajería
- [x] Docker Compose con RabbitMQ
- [x] Docker Compose con Kafka + Zookeeper
- [x] Kafka UI para monitoreo
- [x] Redis para cache distribuido
- [x] PostgreSQL persistente

### ✅ 3. Código Base - ServiceAgent
- [x] Clase base ServiceAgent.java
- [x] Manejo de comandos (RabbitMQ)
- [x] Manejo de eventos (Kafka)
- [x] Estado local del agente
- [x] Auditoría automática

### ✅ 4. Código Base - ServiceOrchestrator
- [x] Orquestador central
- [x] Enrutamiento de mensajes
- [x] Circuit breakers
- [x] Monitoreo de salud
- [x] Orquestación de flujos

### ✅ 5. Dependencias Maven
- [x] RabbitMQ Spring AMQP
- [x] Kafka Spring
- [x] Redis Spring Data
- [x] Resilience4j
- [x] Spring Cloud Bus

---

## 🏗️ ARQUITECTURA IMPLEMENTADA

```
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY                                │
│                  (Orchestrator Enabled)                         │
└────────────────────────────────────────────────────────────────┘
                              │
                ┌─────────────┼─────────────┐
                │             │             │
                ▼             ▼             ▼
          RabbitMQ       Kafka Topics    Redis Cache
        (Queues)        (Events)        (State)
                │             │             │
                └─────────────┼─────────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
            ▼                 ▼                 ▼
      AUTH AGENT      SENSOR AGENT      ALERT AGENT
      (Listens:      (Listens:          (Listens:
       auth.q)       sensor.q)          sensor.data)
      (Publishes:    (Publishes:        (Publishes:
       auth.events)  sensor.data)       alert.events)
```

---

## 🤖 AGENTES CREADOS

### ServiceAgent Base Class
```java
✅ Clase abstracta para todos los agentes
✅ Escuchar comandos en RabbitMQ
✅ Procesar eventos de Kafka
✅ Mantener estado distribuido en Redis
✅ Registrar auditoría en Kafka (inmutable)
✅ Health checks automáticos
```

### Estructura del Agente

```
ServiceAgent
├─ handleCommand(CommandMessage)
│  └─ RabbitMQ listener
├─ handleEvent(EventMessage)
│  └─ Kafka listener
├─ publishCommand(queue, command)
│  └─ RabbitMQ publisher
├─ publishEvent(topic, event)
│  └─ Kafka publisher
├─ updateState(key, value)
│  └─ Redis cache
└─ getHealth()
   └─ Status report
```

---

## 🎯 ORQUESTADOR CENTRAL

### ServiceOrchestrator
```java
✅ Registración de servicios
✅ Enrutamiento de comandos
✅ Orquestación de flujos distribuidos
✅ Monitoreo de salud (cada 30s)
✅ Circuit breakers por servicio
✅ Seguimiento de transacciones
✅ Auditoría de operaciones
```

### Flujo del Orquestador

```
1. Cliente envía solicitud
   │
2. Gateway recibe → Orquestador
   │
3. Orquestador valida circuit breaker
   │
4. Envía comando a servicio via RabbitMQ
   │
5. Servicio procesa → publica evento en Kafka
   │
6. Orquestador captura evento
   │
7. Coordina siguiente paso (si hay)
   │
8. Responde al cliente
```

---

## 📨 FLUJOS DE COMUNICACIÓN

### Flujo 1: Autenticación (Síncrono - RabbitMQ)

```
Cliente
  │
  ├─ POST /login
  │
  ▼
API Gateway
  │
  ├─ Orchestrator.sendCommand("auth-service", "LOGIN", payload)
  │
  ▼
RabbitMQ: auth.command.q
  │
  ▼
AUTH_AGENT
  │
  ├─ handleCommand(command)
  ├─ Valida credenciales
  ├─ Genera JWT
  │
  ├─ publishEvent("auth.events", event)
  │
  ▼
Kafka: auth.events
  │
  ├─ Orchestrator captura evento
  ├─ Responde al cliente
  │
  ▼
Cliente recibe JWT ✅
```

### Flujo 2: Sensor → Alert (Asíncrono - Kafka)

```
SENSOR_AGENT
  │
  ├─ Lee datos del sensor
  │
  ├─ publishEvent("sensor.data", event)
  │
  ▼
Kafka: sensor.data
  │
  ├─ Múltiples subscribers
  │
  ├─────────────────┬──────────────┬──────────────┐
  │                 │              │              │
  ▼                 ▼              ▼              ▼
ALERT_AGENT    GRAPH_AGENT    STORAGE_AGENT   ANALYTICS_AGENT
  │
  ├─ Detecta anomalía
  │
  ├─ publishEvent("alert.triggered", event)
  │
  ▼
Kafka: alert.triggered
  │
  ▼
NOTIFICATION_AGENT
  │
  ├─ handleEvent(event)
  ├─ Envía email/SMS
  ├─ publishEvent("notification.sent", event)
  │
  ▼
Kafka: notification.sent
  │
  ├─ Auditoría y logs
```

---

## 📊 CONFIGURACIÓN DE SERVICIOS

### RabbitMQ Configuration
```yaml
rabbitmq:
  host: rabbitmq
  port: 5672
  username: guest
  password: guest

# Queues
queues:
  - auth.command.q
  - sensor.command.q
  - alert.command.q
  - access.command.q
  - notification.command.q
```

### Kafka Configuration
```yaml
kafka:
  brokers: kafka:9092
  
# Topics
topics:
  - auth.events (3 partitions)
  - sensor.data (5 partitions)
  - alert.triggered (3 partitions)
  - notification.sent (3 partitions)
  - audit.log (1 partition, no delete policy)
  - orchestrator.events (3 partitions)
```

### Redis Configuration
```yaml
redis:
  host: redis
  port: 6379
  password: redis_secure_password
  
# Keys pattern
keys:
  - agent::{service}::state
  - transaction::{txn_id}
  - circuit_breaker::{service}
```

---

## 🔐 SEGURIDAD EN ARQUITECTURA SDD

### Layer 1: Autenticación de Agentes
```
✅ Cada agente tiene API key único
✅ Certificados SSL/TLS
✅ JWT de sesión por agente
```

### Layer 2: Encriptación de Mensajes
```
RabbitMQ:
✅ SSL/TLS connection
✅ Mensaje encriptado en payload

Kafka:
✅ SSL/TLS
✅ SASL/SCRAM authentication
✅ Encriptación end-to-end
```

### Layer 3: Auditoría Inmutable
```
✅ Kafka topic "audit.log" (no-delete)
✅ Quién ejecutó → Agent ID
✅ Qué ejecutó → Comando/Evento
✅ Cuándo → Timestamp
✅ Resultado → Success/Failure
```

### Layer 4: Aislamiento de Agentes
```
✅ Cada agente en contenedor aparte
✅ Usuario no-root
✅ Red aislada (stark-network)
✅ Health checks automáticos
```

---

## 📦 INICIO RÁPIDO

### 1. Usar el nuevo docker-compose

```bash
# Con todas las dependencias (SDD)
docker-compose -f docker-compose-sdd.yml up -d

# O con el anterior (compatible)
docker-compose up -d
```

### 2. Verificar servicios

```bash
# Ver todos los contenedores
docker-compose ps

# Verificar RabbitMQ Management UI
curl http://localhost:15672
# user: guest / pass: guest

# Verificar Kafka UI
curl http://localhost:8888

# Verificar Redis
redis-cli -h localhost -p 6379 ping
```

### 3. Ver logs de agentes

```bash
# Auth Agent
docker-compose logs -f auth-service

# Sensor Agent
docker-compose logs -f sensor-service

# Verificar orquestación
docker-compose logs -f gateway
```

---

## 🧪 PRUEBAS DE SDD

### Test 1: Comunicación RabbitMQ

```bash
# Enviar comando a RabbitMQ
rabbitmqctl publish_message auth.command.q \
  '{"command_id":"123","command_type":"LOGIN","payload":{}}'

# Verificar en AgentLog
docker-compose logs auth-service | grep "Command received"
```

### Test 2: Eventos en Kafka

```bash
# Monitorear topic de eventos
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic auth.events \
  --from-beginning

# Luego hacer login y ver evento
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@Secure2024!"}'
```

### Test 3: Circuit Breaker

```bash
# Iniciar fallo en un servicio
# (matar contenedor de sensor)

docker stop stark-sensor-service

# Intentar enviar comando
# (debería activar circuit breaker)

curl -X GET http://localhost:8080/sensors

# Ver estado del circuit breaker en logs
docker-compose logs gateway | grep "Circuit breaker"
```

---

## 📊 MÉTRICAS Y MONITOREO

### Dashboards Disponibles

1. **RabbitMQ Management**
   - URL: http://localhost:15672
   - Queues, Connections, Channels

2. **Kafka UI**
   - URL: http://localhost:8888
   - Topics, Partitions, Consumer Groups

3. **Redis Insight** (opcional)
   - Ver keys y valores en cache

### Métricas Clave

```yaml
Service Orchestrator:
  - Servicios registrados: 8
  - Transacciones activas: X
  - Circuit breakers: 8 (CLOSED)
  - Auditoría eventos: X por minuto

Service Agents:
  - Status: READY
  - Último latido: < 30s
  - Mensajes procesados: X
  - Errores: 0

Message Brokers:
  - RabbitMQ: Queues 5, Messages X
  - Kafka: Topics 6, Partitions 18
  - Redis: Keys X, Memory X MB
```

---

## 🚀 PRÓXIMOS PASOS

### Hoy (Día 2 - Continuación):
- [ ] Implementar AUTH_AGENT con messaging
- [ ] Implementar SENSOR_AGENT
- [ ] Tests de flujos básicos

### Mañana (Día 3):
- [ ] Implementar ALERT_AGENT
- [ ] Implementar NOTIFICATION_AGENT
- [ ] Validación de eventos en Kafka
- [ ] Tests de flujos complejos

### Próximo:
- [ ] Monitoreo avanzado
- [ ] Observabilidad (traces distribuidas)
- [ ] Disaster recovery
- [ ] Performance tuning

---

## 📈 VENTAJAS DE ESTA ARQUITECTURA

✅ **Escalabilidad Horizontal**
- Agregar instancias de agentes sin modificar existentes
- Load balancing automático

✅ **Resilencia**
- Fallo de un agente no afecta otros
- Reintentos automáticos
- Circuit breakers integrados

✅ **Observabilidad**
- Auditoría completa de todas las operaciones
- Trazas distribuidas
- Monitoreo de salud centralizado

✅ **Desacoplamiento**
- Agentes completamente independientes
- Comunicación asíncrona
- Versionamiento de eventos

✅ **Performance**
- Procesamiento paralelo
- Colas con backpressure
- Streaming de datos en tiempo real
- Cache distribuido

---

## 📚 DOCUMENTACIÓN RELACIONADA

- `ARCHITECTURE_SDD_ANALYSIS.md` - Análisis completo
- `docker-compose-sdd.yml` - Configuración completa
- `ServiceAgent.java` - Clase base de agentes
- `ServiceOrchestrator.java` - Orquestador central

---

**Status:** 🟢 DÍA 2 EN PROGRESO  
**Próximo:** Implementar agentes específicos con JWT


