package com.distribuidos.stark.starkDistribuidos_access.agent;

import com.distribuidos.stark.agent.ServiceAgent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ✅ ACCESS CONTROL AGENT
 * 
 * Responsabilidades:
 * • Procesar comandos de control de acceso
 * • Verificar permisos de usuario
 * • Registrar intentos de acceso
 * • Auditar todas las operaciones
 * • Publicar eventos de acceso a Kafka
 * 
 * Comunicación:
 * • Entrada: RabbitMQ (access.command.q)
 * • Salida: Kafka (access.events)
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class AccessControlAgent extends ServiceAgent {

    @Override
    protected void onInitialize() {
        log.info("🔐 AccessControlAgent initialized");
        updateState("agent_type", "ACCESS_CONTROL");
        updateState("commands_processed", 0L);
        updateState("access_denied_count", 0L);
    }

    /**
     * ✅ Escuchar comandos de acceso desde RabbitMQ
     */
    @RabbitListener(queues = "access.command.q")
    public void handleAccessCommand(String message) {
        try {
            CommandMessage command = objectMapper.readValue(message, CommandMessage.class);
            log.info("📨 AccessControlAgent: Received command: {}", command.getCommandType());
            handleCommand(command);
        } catch (Exception e) {
            log.error("Error deserializing command", e);
        }
    }

    /**
     * ✅ Escuchar eventos de otros agentes
     */
    @KafkaListener(topics = "auth.events", groupId = "access-service")
    public void handleAuthEvent(String message) {
        try {
            EventMessage event = objectMapper.readValue(message, EventMessage.class);
            log.info("📌 AccessControlAgent: Received event from auth: {}", event.getEventType());
            handleEvent(event);
        } catch (Exception e) {
            log.error("Error deserializing event", e);
        }
    }

    /**
     * ✅ Procesar comando de acceso
     */
    @Override
    protected void processCommand(CommandMessage command) throws Exception {
        updateStatus(AgentStatus.PROCESSING);
        
        String commandType = command.getCommandType();
        Map<String, Object> payload = command.getPayload();

        switch (commandType) {
            case "CHECK_ACCESS":
                handleCheckAccess(command.getCommandId(), payload);
                break;
            case "GRANT_PERMISSION":
                handleGrantPermission(command.getCommandId(), payload);
                break;
            case "REVOKE_PERMISSION":
                handleRevokePermission(command.getCommandId(), payload);
                break;
            case "LIST_PERMISSIONS":
                handleListPermissions(command.getCommandId(), payload);
                break;
            default:
                log.warn("Unknown command type: {}", commandType);
        }
        
        updateStatus(AgentStatus.READY);
        incrementProcessedCommands();
    }

    /**
     * ✅ Manejar evento de autenticación exitosa
     */
    @Override
    protected void processEvent(EventMessage event) throws Exception {
        String eventType = event.getEventType();
        
        if ("USER_AUTHENTICATED".equals(eventType)) {
            Map<String, Object> data = event.getData();
            String userId = (String) data.get("user_id");
            String resource = (String) data.get("requested_resource");
            
            log.info("✅ User {} authenticated, checking access to {}", userId, resource);
            
            // Verificar acceso automáticamente
            boolean hasAccess = verifyAccess(userId, resource);
            
            if (hasAccess) {
                publishAccessGrantedEvent(userId, resource);
            } else {
                publishAccessDeniedEvent(userId, resource);
            }
        }
    }

    /**
     * ✅ Verificar acceso a recurso
     */
    private boolean verifyAccess(String userId, String resource) {
        // TODO: Integrar con base de datos de permisos
        // Por ahora simulamos verificación
        
        log.info("🔍 Verifying access for user {} to resource {}", userId, resource);
        
        // Simulación: admin siempre tiene acceso
        if ("admin".equals(userId)) {
            return true;
        }
        
        // Verificar en el estado si tiene permiso
        Map<String, Set<String>> permissions = getPermissions();
        Set<String> userPerms = permissions.getOrDefault(userId, new HashSet<>());
        
        return userPerms.contains(resource) || userPerms.contains("*");
    }

    /**
     * ✅ Manejar comando CHECK_ACCESS
     */
    private void handleCheckAccess(String commandId, Map<String, Object> payload) {
        String userId = (String) payload.get("user_id");
        String resource = (String) payload.get("resource");
        
        log.info("🔐 Checking access - User: {}, Resource: {}", userId, resource);
        
        boolean hasAccess = verifyAccess(userId, resource);
        
        if (hasAccess) {
            publishAccessGrantedEvent(userId, resource);
        } else {
            publishAccessDeniedEvent(userId, resource);
            incrementDeniedAccess();
        }
    }

    /**
     * ✅ Manejar comando GRANT_PERMISSION
     */
    private void handleGrantPermission(String commandId, Map<String, Object> payload) {
        String userId = (String) payload.get("user_id");
        String resource = (String) payload.get("resource");
        
        log.info("✅ Granting permission - User: {}, Resource: {}", userId, resource);
        
        Map<String, Set<String>> permissions = getPermissions();
        permissions.computeIfAbsent(userId, k -> new HashSet<>()).add(resource);
        
        updateState("permissions", permissions);
        
        // Publicar evento
        EventMessage event = new EventMessage();
        event.setEventType("PERMISSION_GRANTED");
        event.setSource("access-service");
        event.setData(payload);
        publishEvent("access.events", event);
    }

    /**
     * ✅ Manejar comando REVOKE_PERMISSION
     */
    private void handleRevokePermission(String commandId, Map<String, Object> payload) {
        String userId = (String) payload.get("user_id");
        String resource = (String) payload.get("resource");
        
        log.info("❌ Revoking permission - User: {}, Resource: {}", userId, resource);
        
        Map<String, Set<String>> permissions = getPermissions();
        if (permissions.containsKey(userId)) {
            permissions.get(userId).remove(resource);
        }
        
        updateState("permissions", permissions);
        
        // Publicar evento
        EventMessage event = new EventMessage();
        event.setEventType("PERMISSION_REVOKED");
        event.setSource("access-service");
        event.setData(payload);
        publishEvent("access.events", event);
    }

    /**
     * ✅ Manejar comando LIST_PERMISSIONS
     */
    private void handleListPermissions(String commandId, Map<String, Object> payload) {
        String userId = (String) payload.get("user_id");
        
        log.info("📋 Listing permissions for user: {}", userId);
        
        Map<String, Set<String>> permissions = getPermissions();
        Set<String> userPerms = permissions.getOrDefault(userId, new HashSet<>());
        
        EventMessage event = new EventMessage();
        event.setEventType("PERMISSIONS_LISTED");
        event.setSource("access-service");
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("permissions", userPerms);
        event.setData(data);
        publishEvent("access.events", event);
    }

    /**
     * ✅ Publicar evento de acceso concedido
     */
    private void publishAccessGrantedEvent(String userId, String resource) {
        EventMessage event = new EventMessage();
        event.setEventType("ACCESS_GRANTED");
        event.setSource("access-service");
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("resource", resource);
        data.put("timestamp", System.currentTimeMillis());
        event.setData(data);
        
        publishEvent("access.events", event);
        log.info("✅ ACCESS_GRANTED event published");
    }

    /**
     * ✅ Publicar evento de acceso denegado
     */
    private void publishAccessDeniedEvent(String userId, String resource) {
        EventMessage event = new EventMessage();
        event.setEventType("ACCESS_DENIED");
        event.setSource("access-service");
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("resource", resource);
        data.put("timestamp", System.currentTimeMillis());
        event.setData(data);
        
        publishEvent("access.events", event);
        log.warn("❌ ACCESS_DENIED event published");
    }

    /**
     * ✅ Obtener permisos del estado
     */
    @SuppressWarnings("unchecked")
    private Map<String, Set<String>> getPermissions() {
        Map<String, Set<String>> permissions = 
            (Map<String, Set<String>>) getState("permissions");
        
        if (permissions == null) {
            permissions = new HashMap<>();
            // Inicializar con permisos por defecto
            permissions.put("admin", new HashSet<>(Arrays.asList("*")));
            permissions.put("user1", new HashSet<>(Arrays.asList("sensors", "alerts")));
            updateState("permissions", permissions);
        }
        
        return permissions;
    }

    /**
     * ✅ Incrementar contador de comandos procesados
     */
    private void incrementProcessedCommands() {
        Long count = (Long) getState("commands_processed");
        if (count == null) count = 0L;
        updateState("commands_processed", count + 1);
    }

    /**
     * ✅ Incrementar contador de accesos denegados
     */
    private void incrementDeniedAccess() {
        Long count = (Long) getState("access_denied_count");
        if (count == null) count = 0L;
        updateState("access_denied_count", count + 1);
    }
}

