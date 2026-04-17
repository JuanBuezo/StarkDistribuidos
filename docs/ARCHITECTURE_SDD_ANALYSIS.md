# 🏗️ ARQUITECTURA SDD CON AGENTES - DISEÑO Y ANÁLISIS

**Fecha:** 17 de Abril de 2026  
**Fase:** DÍA 2 - Arquitectura Distribuida Avanzada  
**Status:** ✅ DISEÑO COMPLETADO

---

## 📋 TABLA DE CONTENIDOS

1. [Arquitectura SDD](#arquitectura-sdd)
2. [Componentes](#componentes)
3. [Flujo de Comunicación](#flujo-de-comunicación)
4. [Agentes por Servicio](#agentes-por-servicio)
5. [Orquestador](#orquestador)
6. [Implementación](#implementación)

---

## 🏗️ ARQUITECTURA SDD

### Concepto

**Software-Defined Deployment (SDD)** es una arquitectura que permite:
- ✅ Cada microservicio tiene su propio agente autónomo
- ✅ Orquestador central gestiona la comunicación
- ✅ Desacoplamiento total entre servicios
- ✅ Escalabilidad horizontal
- ✅ Resiliencia y fault-tolerance

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ORQUESTADOR CENTRAL                         │
│  (Service Orchestrator - Coordinator)                               │
│  • Rutas mensajes                                                   │
│  • Maneja fallos                                                    │
│  • Coordina flujos                                                  │
│  • Monitorea salud                                                  │
└────────────┬──────────────────────────────────────────────┬─────────┘
             │                                              │
      ┌──────▼─────┐                                ┌──────▼─────┐
      │ RabbitMQ   │◄──────────────────────────────►│ Kafka     │
      │ (Queues)   │                                │ (Topics)  │
      │ • Requests │                                │ • Events  │
      │ • Commands │                                │ • Streams │
      └──────┬──────────────────────────────────────────────┘
             │
    ┌────────┴────────────────────────────────────────────────┐
    │                    AGENTES POR SERVICIO                 │
    │                                                          │
    ├─────────────────┬─────────────────┬────────────────────┤
    │                 │                 │                    │
    ▼                 ▼                 ▼                    ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ AUTH     │   │ SENSOR   │   │ ALERT    │   │ ACCESS   │
│ AGENT    │   │ AGENT    │   │ AGENT    │   │ AGENT    │
│          │   │          │   │          │   │          │
│ • Process│   │ • Collect│   │ • Detect │   │ • Log    │
│ • Auth   │   │ • Send   │   │ • Notify │   │ • Check  │
│ • JWT    │   │ • Queue  │   │ • Queue  │   │ • Queue  │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
    ▲              ▲              ▲              ▲
    │              │              │              │
    └──────────────┴──────────────┴──────────────┘
         Microservicio Spring Boot + Agent
```

---

## 🔧 COMPONENTES

### 1. Orquestador Central (Service Orchestrator)

```
ServiceOrchestrator
├─ MessageRouter
│  ├─ RabbitMQ Router
│  └─ Kafka Router
├─ WorkflowEngine
│  ├─ ExecutionPlan
│  └─ StateManager
├─ HealthMonitor
│  ├─ AgentHealth
│  └─ QueueHealth
└─ FaultHandler
   ├─ RetryPolicy
   └─ CircuitBreaker
```

### 2. Agente por Servicio

```
ServiceAgent
├─ MessageListener
│  ├─ RabbitMQ Listener
│  └─ Kafka Listener
├─ BusinessLogic
│  ├─ Service Core
│  └─ Domain Logic
├─ MessageProducer
│  ├─ RabbitMQ Publisher
│  └─ Kafka Publisher
└─ StateManagement
   ├─ Local State
   └─ Distributed Cache
```

### 3. Message Brokers

**RabbitMQ:**
- Punto-a-punto (queues)
- Comandos
- Solicitudes síncronas
- Garantía de entrega

**Kafka:**
- Publicador-suscriptor (topics)
- Eventos
- Streams de datos
- Auditoría e historial

---

## 📨 FLUJO DE COMUNICACIÓN

### Escenario 1: Autenticación → Acceso (Síncrono con RabbitMQ)

```
┌──────────────┐
│  Cliente     │
└────────┬─────┘
         │ POST /login
         ▼
┌──────────────────────────────────────────────────────┐
│  API Gateway                                         │
│  • Recibe solicitud                                  │
│  • Envía comando a queue                             │
└────────┬───────────────────────────────────────────┬─┘
         │                                           │
         ▼                                           ▼
   auth.command.q                           access.command.q
    RabbitMQ                                  RabbitMQ
         │                                           │
         ▼                                           ▼
┌──────────────────────────┐         ┌──────────────────────────┐
│ AUTH AGENT               │         │ ACCESS AGENT             │
│ • Valida credenciales    │         │ • Verifica permisos      │
│ • Genera JWT             │         │ • Registra acceso        │
│ • Publica evento         │         │ • Responde               │
└────────┬────────────────┘         └────────┬─────────────────┘
         │                                    │
         ▼                                    ▼
   auth.events                      access.events
   Kafka Topic                       Kafka Topic
         │                                    │
         └────────────────┬───────────────────┘
                          ▼
                   Otros servicios
                   (Escuchan eventos)
```

### Escenario 2: Sensor → Alert → Notification (Asíncrono con Kafka)

```
┌──────────────────────────────────────────────────────┐
│ SENSOR AGENT                                         │
│ • Lee datos                                          │
│ • Publica evento sensor.data                         │
└───────────┬──────────────────────────────────────────┘
            │
            ▼
      sensor.data (Kafka)
            │
    ┌───────┼────────┐
    │       │        │
    ▼       ▼        ▼
┌────────┐ ┌────────┐ ┌────────┐
│ ALERT  │ │GRAPH   │ │STORAGE │
│ AGENT  │ │AGENT   │ │AGENT   │
│Detecta │ │Visualiz│ │Persiste│
│anoma-  │ │a       │ │datos   │
│lías    │ │        │ │        │
└───┬────┘ └────────┘ └────────┘
    │
    ▼
alert.triggered
    │
    ▼
┌──────────────────────────────────────────────────────┐
│ NOTIFICATION AGENT                                   │
│ • Env emails                                         │
│ • Publica eventos                                    │
│ • Registra log                                       │
└──────────────────────────────────────────────────────┘
```

---

## 🤖 AGENTES POR SERVICIO

### Estructura General del Agente

```java
@Service
public abstract class ServiceAgent {
    
    // Escuchar mensajes
    @RabbitListener(queues = "service.command.q")
    public void handleCommand(Message message) { }
    
    @KafkaListener(topics = "service.events")
    public void handleEvent(Event event) { }
    
    // Enviar mensajes
    public void publishCommand(String queue, Message msg) { }
    public void publishEvent(String topic, Event evt) { }
    
    // Lógica de negocio
    protected abstract void processCommand(Message msg);
    protected abstract void processEvent(Event evt);
    
    // Estado del agente
    private AgentState state;
    public void updateState(AgentState newState) { }
}
```

### Servicios con Agentes

1. **AUTH_AGENT**
   - Entrada: `auth.command.q` (RabbitMQ)
   - Salida: `auth.events` (Kafka)
   - Responsabilidad: Autenticación y JWT

2. **SENSOR_AGENT**
   - Entrada: `sensor.command.q` (RabbitMQ)
   - Salida: `sensor.data` (Kafka)
   - Responsabilidad: Procesar datos de sensores

3. **ALERT_AGENT**
   - Entrada: `sensor.data` (Kafka)
   - Salida: `alert.triggered` (Kafka)
   - Responsabilidad: Detectar anomalías

4. **ACCESS_AGENT**
   - Entrada: `access.command.q` (RabbitMQ)
   - Salida: `access.events` (Kafka)
   - Responsabilidad: Control de acceso

5. **NOTIFICATION_AGENT**
   - Entrada: `alert.triggered` (Kafka)
   - Salida: `notification.sent` (Kafka)
   - Responsabilidad: Enviar notificaciones

---

## 🎯 ORQUESTADOR

### Responsabilidades del Orquestador

```java
@Service
public class ServiceOrchestrator {
    
    // 1. Enrutamiento de mensajes
    public void routeMessage(Message msg, String destination) { }
    
    // 2. Orquestación de flujos
    public void orchestrateWorkflow(String workflowId, 
                                   List<String> agents) { }
    
    // 3. Monitoreo de salud
    @Scheduled(fixedRate = 5000)
    public void monitorAgentHealth() { }
    
    // 4. Manejo de fallos
    public void handleAgentFailure(String agentId) { }
    
    // 5. Seguimiento de transacciones
    public void trackTransaction(String txnId, List<Step> steps) { }
}
```

### Flujo del Orquestador

```
1. Recibe solicitud
   ↓
2. Identifica flujo necesario
   ↓
3. Construye plan de ejecución
   ↓
4. Envía comandos a agentes (RabbitMQ)
   ↓
5. Escucha eventos de agentes (Kafka)
   ↓
6. Coordina estados
   ↓
7. Maneja errores/reintentos
   ↓
8. Responde al cliente
```

---

## 📊 MATRIZ DE COMUNICACIÓN

| Origen | Destino | Broker | Tipo | Garantía |
|--------|---------|--------|------|----------|
| API Gateway | AUTH_AGENT | RabbitMQ | Command | Entrega garantizada |
| AUTH_AGENT | Otros | Kafka | Event | Registro permanente |
| SENSOR | ALERT | Kafka | Stream | Procesamiento en tiempo real |
| ALERT | NOTIFICATION | Kafka | Event | Auditoría |
| CLIENT | Orquestador | REST | Sync | HTTP |
| Orquestador | Agentes | Dual | Async | Confiable |

---

## 🔐 SEGURIDAD EN ARQUITECTURA SDD

### Nivel 1: Autenticación de Agentes
```
Cada agente tiene:
├─ API Key único
├─ Certificado SSL/TLS
└─ Token JWT de sesión
```

### Nivel 2: Encriptación de Mensajes
```
RabbitMQ:
├─ SSL/TLS en conexión
└─ Mensaje encriptado

Kafka:
├─ SSL/TLS
├─ Autenticación SASL
└─ Encriptación end-to-end
```

### Nivel 3: Auditoría
```
Todos los eventos → Kafka (inmutable)
├─ Quién ejecutó
├─ Qué ejecutó
├─ Cuándo
└─ Resultado
```

---

## 📦 DEPENDENCIAS NECESARIAS

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

<!-- Orquestación -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-bus</artifactId>
</dependency>

<!-- Resilencia -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-core</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Estado distribuido -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
</dependency>
```

---

## 🚀 VENTAJAS DE ESTA ARQUITECTURA

✅ **Escalabilidad:**
- Agregar nuevos agentes sin modificar existentes
- Balanceo de carga automático

✅ **Resiliencia:**
- Fallo de un agente no afecta otros
- Reintentos automáticos
- Circuit breakers

✅ **Observabilidad:**
- Auditoría completa en Kafka
- Trazas distribuidas
- Monitoreo de salud

✅ **Desacoplamiento:**
- Agentes independientes
- Comunicación asíncrona
- Versionamiento de eventos

✅ **Performance:**
- Procesamiento paralelo
- Colas con backpressure
- Streaming de datos

---

## 📅 PLAN DE IMPLEMENTACIÓN (DÍA 2-3)

### Hoy (Día 2):
- [ ] Crear infraestructura (RabbitMQ, Kafka, Redis)
- [ ] Implementar ServiceAgent base
- [ ] Crear ServiceOrchestrator
- [ ] Implementar AUTH_AGENT con messaging

### Mañana (Día 3):
- [ ] Implementar SENSOR_AGENT
- [ ] Implementar ALERT_AGENT
- [ ] Tests de flujos
- [ ] Validación de eventos

### Próximo:
- [ ] Monitoreo y observabilidad
- [ ] Disaster recovery
- [ ] Optimización de performance

---

## 📊 COMPARATIVA: Antes vs Después

### Antes (Día 1)
- Microservicios independientes
- HTTP REST directo
- Acoplamiento temporal
- Difícil de escalar

### Después (Día 2+)
- Agentes autónomos
- Comunicación por eventos
- Completamente desacoplado
- Escalabilidad horizontal
- Auditoría completa
- Resiliencia integrada

---

**Estado:** 🟢 ANÁLISIS COMPLETO - LISTO PARA IMPLEMENTAR


