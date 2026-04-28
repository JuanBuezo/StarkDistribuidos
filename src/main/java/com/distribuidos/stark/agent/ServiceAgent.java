package com.distribuidos.stark.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ ARCHITECTURE SDD - Service Agent Base Class
 * 
 * Cada microservicio hereda de esta clase para actuar como agente autónomo.
 * 
 * Responsabilidades:
 * • Escuchar comandos en RabbitMQ
 * • Procesar eventos de Kafka
 * • Mantener estado local
 * • Publicar eventos
 * • Registrar auditoría
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Component
public abstract class ServiceAgent {

    protected static final Logger log = LoggerFactory.getLogger(ServiceAgent.class);

    @Value("${service.name:unknown}")
    protected String serviceName;

    @Value("${service.agent.enabled:true}")
    protected boolean agentEnabled;

    @Autowired(required = false)
    protected RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    protected KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    // Estado local del agente
    protected final Map<String, Object> agentState = new ConcurrentHashMap<>();
    protected AgentStatus status = AgentStatus.INITIALIZING;
    protected LocalDateTime lastHeartbeat = LocalDateTime.now();

    /**
     * ✅ Inicializar agente
     */
    public void initialize() {
        if (!agentEnabled) {
            log.info("Agent disabled for service: {}", serviceName);
            return;
        }

        log.info("🤖 Initializing Agent for service: {}", serviceName);
        
        updateStatus(AgentStatus.READY);
        agentState.put("service_name", serviceName);
        agentState.put("initialized_at", LocalDateTime.now());
        
        onInitialize();
        log.info("✅ Agent initialized: {}", serviceName);
    }

    /**
     * ✅ Hook para inicialización de subclases
     */
    protected abstract void onInitialize();

    /**
     * ✅ Procesar comando desde RabbitMQ
     */
    public void handleCommand(CommandMessage command) {
        log.info("📨 Agent [{}] received command: {}", serviceName, command.getCommandType());
        
        if (status != AgentStatus.READY) {
            log.warn("Agent not ready. Status: {}", status);
            return;
        }

        try {
            processCommand(command);
            recordAudit(command.getCommandId(), "COMMAND_PROCESSED", "SUCCESS");
        } catch (Exception e) {
            log.error("❌ Error processing command: {}", e.getMessage());
            recordAudit(command.getCommandId(), "COMMAND_FAILED", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * ✅ Procesar evento desde Kafka
     */
    public void handleEvent(EventMessage event) {
        log.info("📌 Agent [{}] received event: {}", serviceName, event.getEventType());
        
        try {
            processEvent(event);
            recordAudit(event.getEventId(), "EVENT_PROCESSED", "SUCCESS");
        } catch (Exception e) {
            log.error("❌ Error processing event: {}", e.getMessage());
            recordAudit(event.getEventId(), "EVENT_FAILED", e.getMessage());
        }
    }

    /**
     * ✅ Procesar lógica del comando (implementar en subclases)
     */
    protected abstract void processCommand(CommandMessage command) throws Exception;

    /**
     * ✅ Procesar lógica del evento (implementar en subclases)
     */
    protected abstract void processEvent(EventMessage event) throws Exception;

    /**
     * ✅ Publicar comando a otro servicio
     */
    protected void publishCommand(String queue, CommandMessage command) {
        if (rabbitTemplate == null) {
            log.error("RabbitMQ not configured");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(command);
            rabbitTemplate.convertAndSend(queue, json);
            log.info("✉️  Command sent to queue [{}]: {}", queue, command.getCommandType());
            recordAudit(command.getCommandId(), "COMMAND_SENT", queue);
        } catch (Exception e) {
            log.error("❌ Error sending command: {}", e.getMessage());
        }
    }

    /**
     * ✅ Publicar evento a Kafka
     */
    protected void publishEvent(String topic, EventMessage event) {
        if (kafkaTemplate == null) {
            log.error("Kafka not configured");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getEventId(), json);
            log.info("📤 Event published to topic [{}]: {}", topic, event.getEventType());
            recordAudit(event.getEventId(), "EVENT_PUBLISHED", topic);
        } catch (Exception e) {
            log.error("❌ Error publishing event: {}", e.getMessage());
        }
    }

    /**
     * ✅ Actualizar estado del agente
     */
    protected void updateState(String key, Object value) {
        agentState.put(key, value);
        log.debug("State updated: {} = {}", key, value);
    }

    /**
     * ✅ Obtener estado del agente
     */
    protected Object getState(String key) {
        return agentState.getOrDefault(key, null);
    }

    /**
     * ✅ Actualizar estado del agente
     */
    protected void updateStatus(AgentStatus newStatus) {
        this.status = newStatus;
        this.lastHeartbeat = LocalDateTime.now();
        log.info("Agent status: {} → {}", status.name(), newStatus.name());
    }

    /**
     * ✅ Registrar auditoría
     */
    protected void recordAudit(String txnId, String action, String details) {
        Map<String, Object> audit = new HashMap<>();
        audit.put("timestamp", LocalDateTime.now());
        audit.put("service", serviceName);
        audit.put("transaction_id", txnId);
        audit.put("action", action);
        audit.put("details", details);
        
        // Publicar a Kafka para auditoría inmutable
        try {
            String json = objectMapper.writeValueAsString(audit);
            kafkaTemplate.send("audit.log", txnId, json);
        } catch (Exception e) {
            log.warn("Could not record audit: {}", e.getMessage());
        }
    }

    /**
     * ✅ Health check del agente
     */
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", serviceName);
        health.put("status", status.name());
        health.put("last_heartbeat", lastHeartbeat);
        health.put("state", agentState);
        return health;
    }

    /**
     * ✅ Estados posibles del agente
     */
    public enum AgentStatus {
        INITIALIZING,
        READY,
        PROCESSING,
        ERROR,
        STOPPING
    }

    /**
     * ✅ Mensaje de comando
     */
    public static class CommandMessage {
        private String commandId;
        private String commandType;
        private Map<String, Object> payload;
        private LocalDateTime timestamp;

        // Getters & Setters
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }
        public String getCommandType() { return commandType; }
        public void setCommandType(String commandType) { this.commandType = commandType; }
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public CommandMessage() {
            this.commandId = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now();
        }
    }

    /**
     * ✅ Mensaje de evento
     */
    public static class EventMessage {
        private String eventId;
        private String eventType;
        private String source;
        private Map<String, Object> data;
        private LocalDateTime timestamp;

        // Getters & Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public EventMessage() {
            this.eventId = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now();
        }
    }
}

