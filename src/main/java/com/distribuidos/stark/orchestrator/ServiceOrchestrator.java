package com.distribuidos.stark.orchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ ARCHITECTURE SDD - Service Orchestrator
 * 
 * Orquestador central que:
 * • Coordina comunicación entre agentes
 * • Enruta mensajes
 * • Maneja flujos distribuidos
 * • Monitorea salud de agentes
 * • Implementa circuit breakers
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class ServiceOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ServiceOrchestrator.class);

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Registry de servicios y su estado
    private final Map<String, ServiceRegistry> serviceRegistry = new ConcurrentHashMap<>();
    
    // Seguimiento de transacciones
    private final Map<String, TransactionContext> transactionContexts = new ConcurrentHashMap<>();
    
    // Circuit breakers por servicio
    private final Map<String, CircuitBreakerState> circuitBreakers = new ConcurrentHashMap<>();

    /**
     * ✅ Registrar un servicio/agente
     */
    public void registerService(String serviceName, String commandQueue, String eventTopic) {
        ServiceRegistry registry = new ServiceRegistry(serviceName, commandQueue, eventTopic);
        serviceRegistry.put(serviceName, registry);
        log.info("✅ Service registered: {}", serviceName);
    }

    /**
     * ✅ Enviar comando a un servicio
     */
    public void sendCommand(String serviceName, String commandType, Map<String, Object> payload) {
        ServiceRegistry registry = serviceRegistry.get(serviceName);
        if (registry == null) {
            log.error("❌ Service not found: {}", serviceName);
            return;
        }

        // Verificar circuit breaker
        if (isCircuitBreakerOpen(serviceName)) {
            log.warn("⚠️  Circuit breaker OPEN for service: {}", serviceName);
            return;
        }

        try {
            String txnId = UUID.randomUUID().toString();
            
            // Crear comando
            Map<String, Object> command = new HashMap<>();
            command.put("command_id", txnId);
            command.put("command_type", commandType);
            command.put("payload", payload);
            command.put("timestamp", LocalDateTime.now());

            // Enviar por RabbitMQ
            if (rabbitTemplate != null) {
                String json = objectMapper.writeValueAsString(command);
                rabbitTemplate.convertAndSend(registry.getCommandQueue(), json);
                
                log.info("📨 Command sent to [{}]: {}", serviceName, commandType);
                
                // Registrar transacción
                TransactionContext context = new TransactionContext(txnId, serviceName, commandType);
                transactionContexts.put(txnId, context);
                
                // Registrar en auditoría
                publishAuditEvent(txnId, "COMMAND_SENT", serviceName);
                
                // Marcar éxito en circuit breaker
                recordSuccess(serviceName);
            }
        } catch (Exception e) {
            log.error("❌ Error sending command: {}", e.getMessage());
            recordFailure(serviceName);
        }
    }

    /**
     * ✅ Orquestar flujo distribuido (multiple servicios)
     */
    public void orchestrateWorkflow(String workflowId, List<WorkflowStep> steps) {
        log.info("🔄 Starting workflow: {}", workflowId);

        for (WorkflowStep step : steps) {
            try {
                log.info("  → Executing step: {}", step.getStepName());
                
                sendCommand(
                    step.getTargetService(),
                    step.getCommand(),
                    step.getPayload()
                );

                // Esperar feedback (aquí simplificado, en prod usar async/callbacks)
                Thread.sleep(100);
                
            } catch (Exception e) {
                log.error("❌ Workflow failed at step: {}", step.getStepName());
                publishAuditEvent(workflowId, "WORKFLOW_FAILED", e.getMessage());
                break;
            }
        }

        log.info("✅ Workflow completed: {}", workflowId);
    }

    /**
     * ✅ Monitorear salud de agentes
     */
    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void monitorAgentHealth() {
        log.debug("🏥 Monitoring agent health...");

        for (String serviceName : serviceRegistry.keySet()) {
            try {
                // Enviar health check command
                Map<String, Object> healthPayload = new HashMap<>();
                healthPayload.put("check_type", "health");
                
                sendCommand(serviceName, "HEALTH_CHECK", healthPayload);
                
            } catch (Exception e) {
                log.warn("Health check failed for {}: {}", serviceName, e.getMessage());
                recordFailure(serviceName);
            }
        }
    }

    /**
     * ✅ Implementar Circuit Breaker
     */
    private void recordSuccess(String serviceName) {
        CircuitBreakerState state = circuitBreakers.get(serviceName);
        if (state == null) {
            state = new CircuitBreakerState();
            circuitBreakers.put(serviceName, state);
        }
        state.recordSuccess();
    }

    private void recordFailure(String serviceName) {
        CircuitBreakerState state = circuitBreakers.get(serviceName);
        if (state == null) {
            state = new CircuitBreakerState();
            circuitBreakers.put(serviceName, state);
        }
        state.recordFailure();
    }

    private boolean isCircuitBreakerOpen(String serviceName) {
        CircuitBreakerState state = circuitBreakers.get(serviceName);
        return state != null && state.isOpen();
    }

    /**
     * ✅ Publicar evento de auditoría
     */
    private void publishAuditEvent(String txnId, String action, String details) {
        if (kafkaTemplate == null) return;

        try {
            Map<String, Object> audit = new HashMap<>();
            audit.put("txn_id", txnId);
            audit.put("action", action);
            audit.put("details", details);
            audit.put("timestamp", LocalDateTime.now());

            String json = objectMapper.writeValueAsString(audit);
            kafkaTemplate.send("orchestrator.events", txnId, json);
        } catch (Exception e) {
            log.warn("Could not publish audit event: {}", e.getMessage());
        }
    }

    /**
     * ✅ Obtener estado del orquestador
     */
    public Map<String, Object> getOrchestratorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());
        status.put("registered_services", serviceRegistry.size());
        status.put("active_transactions", transactionContexts.size());
        
        // Estado de circuit breakers
        Map<String, String> cbStatus = new HashMap<>();
        for (Map.Entry<String, CircuitBreakerState> entry : circuitBreakers.entrySet()) {
            cbStatus.put(entry.getKey(), entry.getValue().isOpen() ? "OPEN" : "CLOSED");
        }
        status.put("circuit_breakers", cbStatus);

        return status;
    }

    /**
     * ✅ Registro de servicio
     */
    public static class ServiceRegistry {
        private final String serviceName;
        private final String commandQueue;
        private final String eventTopic;
        private LocalDateTime registeredAt;

        public ServiceRegistry(String serviceName, String commandQueue, String eventTopic) {
            this.serviceName = serviceName;
            this.commandQueue = commandQueue;
            this.eventTopic = eventTopic;
            this.registeredAt = LocalDateTime.now();
        }

        // Getters
        public String getServiceName() { return serviceName; }
        public String getCommandQueue() { return commandQueue; }
        public String getEventTopic() { return eventTopic; }
        public LocalDateTime getRegisteredAt() { return registeredAt; }
    }

    /**
     * ✅ Contexto de transacción
     */
    public static class TransactionContext {
        private final String txnId;
        private final String targetService;
        private final String commandType;
        private final LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String status;

        public TransactionContext(String txnId, String targetService, String commandType) {
            this.txnId = txnId;
            this.targetService = targetService;
            this.commandType = commandType;
            this.createdAt = LocalDateTime.now();
            this.status = "PENDING";
        }

        // Getters
        public String getTxnId() { return txnId; }
        public String getTargetService() { return targetService; }
        public String getCommandType() { return commandType; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * ✅ Estado de Circuit Breaker
     */
    public static class CircuitBreakerState {
        private int failureCount = 0;
        private int successCount = 0;
        private static final int FAILURE_THRESHOLD = 5;
        private static final int SUCCESS_THRESHOLD = 3;
        private boolean open = false;
        private LocalDateTime lastFailureTime;

        public void recordSuccess() {
            successCount++;
            failureCount = 0;
            
            if (open && successCount >= SUCCESS_THRESHOLD) {
                open = false;
                successCount = 0;
                log.info("🟢 Circuit breaker CLOSED");
            }
        }

        public void recordFailure() {
            failureCount++;
            successCount = 0;
            lastFailureTime = LocalDateTime.now();
            
            if (failureCount >= FAILURE_THRESHOLD) {
                open = true;
                log.warn("🔴 Circuit breaker OPEN");
            }
        }

        public boolean isOpen() {
            return open;
        }
    }

    /**
     * ✅ Paso de flujo de trabajo
     */
    public static class WorkflowStep {
        private String stepName;
        private String targetService;
        private String command;
        private Map<String, Object> payload;

        public WorkflowStep(String stepName, String targetService, String command, Map<String, Object> payload) {
            this.stepName = stepName;
            this.targetService = targetService;
            this.command = command;
            this.payload = payload;
        }

        // Getters
        public String getStepName() { return stepName; }
        public String getTargetService() { return targetService; }
        public String getCommand() { return command; }
        public Map<String, Object> getPayload() { return payload; }
    }
}

