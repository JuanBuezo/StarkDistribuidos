package com.distribuidos.stark.starkDistribuidos_auth.agent;

import com.distribuidos.stark.agent.ServiceAgent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ✅ AUTH AGENT
 * 
 * Responsabilidades:
 * • Procesar comandos de autenticación
 * • Validar credenciales
 * • Generar tokens JWT
 * • Registrar intentos de autenticación
 * • Publicar eventos de éxito/fallo
 * 
 * Comunicación:
 * • Entrada: RabbitMQ (auth.command.q)
 * • Salida: Kafka (auth.events)
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class AuthAgent extends ServiceAgent {

    @Override
    protected void onInitialize() {
        log.info("🔐 AuthAgent initialized");
        updateState("agent_type", "AUTH");
        updateState("logins_successful", 0L);
        updateState("logins_failed", 0L);
        initializeUsers();
    }

    /**
     * ✅ Escuchar comandos de autenticación desde RabbitMQ
     */
    @RabbitListener(queues = "auth.command.q")
    public void handleAuthCommand(String message) {
        try {
            CommandMessage command = objectMapper.readValue(message, CommandMessage.class);
            log.info("🔐 AuthAgent: Received command: {}", command.getCommandType());
            handleCommand(command);
        } catch (Exception e) {
            log.error("Error deserializing command", e);
        }
    }

    /**
     * ✅ Procesar comando de autenticación
     */
    @Override
    protected void processCommand(CommandMessage command) throws Exception {
        updateStatus(AgentStatus.PROCESSING);
        
        String commandType = command.getCommandType();
        Map<String, Object> payload = command.getPayload();

        switch (commandType) {
            case "LOGIN":
                handleLogin(command.getCommandId(), payload);
                break;
            case "VALIDATE_TOKEN":
                handleValidateToken(command.getCommandId(), payload);
                break;
            case "REFRESH_TOKEN":
                handleRefreshToken(command.getCommandId(), payload);
                break;
            default:
                log.warn("Unknown command type: {}", commandType);
        }
        
        updateStatus(AgentStatus.READY);
    }

    /**
     * ✅ Procesar evento (no usado normalmente en auth)
     */
    @Override
    protected void processEvent(EventMessage event) throws Exception {
        log.debug("AuthAgent received event: {}", event.getEventType());
    }

    /**
     * ✅ Manejar comando LOGIN
     */
    private void handleLogin(String commandId, Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String password = (String) payload.get("password");
        
        log.info("🔐 Login attempt for user: {}", username);
        
        boolean isValid = validateCredentials(username, password);
        
        if (isValid) {
            String token = generateJWT(username);
            publishLoginSuccessEvent(commandId, username, token);
            incrementSuccessfulLogins();
        } else {
            publishLoginFailureEvent(commandId, username);
            incrementFailedLogins();
        }
    }

    /**
     * ✅ Manejar comando VALIDATE_TOKEN
     */
    private void handleValidateToken(String commandId, Map<String, Object> payload) {
        String token = (String) payload.get("token");
        
        log.info("🔐 Validating token");
        
        boolean isValid = validateJWT(token);
        
        if (isValid) {
            String username = extractUsernameFromToken(token);
            publishTokenValidEvent(commandId, token, username, true);
        } else {
            publishTokenValidEvent(commandId, token, "unknown", false);
        }
    }

    /**
     * ✅ Manejar comando REFRESH_TOKEN
     */
    private void handleRefreshToken(String commandId, Map<String, Object> payload) {
        String oldToken = (String) payload.get("token");
        
        log.info("🔐 Refreshing token");
        
        if (validateJWT(oldToken)) {
            String username = extractUsernameFromToken(oldToken);
            String newToken = generateJWT(username);
            publishTokenRefreshedEvent(commandId, username, newToken);
        } else {
            publishTokenRefreshedEvent(commandId, "unknown", null);
        }
    }

    /**
     * ✅ Validar credenciales
     */
    private boolean validateCredentials(String username, String password) {
        @SuppressWarnings("unchecked")
        Map<String, String> users = (Map<String, String>) getState("users");
        
        if (users == null) {
            log.warn("Users map not initialized");
            return false;
        }
        
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    /**
     * ✅ Generar JWT
     */
    private String generateJWT(String username) {
        // Simulación de generación de JWT
        // En producción, usar io.jsonwebtoken
        String jwt = "eyJhbGciOiJIUzUxMiJ9." +
                    "eyJzdWIiOiIiICsgdXNlcm5hbWUgKyAi" +
                    "IiwiaWF0IjoxNzExMjcwMDAwLCJleHAiOjE3MTEzNTY0MDB9." +
                    "test_signature_" + System.currentTimeMillis();
        
        log.info("✅ JWT generated for user: {}", username);
        return jwt;
    }

    /**
     * ✅ Validar JWT
     */
    private boolean validateJWT(String token) {
        return token != null && token.startsWith("eyJhbGciOiJIUzUxMiJ9.");
    }

    /**
     * ✅ Extraer username del token
     */
    private String extractUsernameFromToken(String token) {
        // Simulación - en producción parsear JWT real
        return "user_from_token";
    }

    /**
     * ✅ Publicar evento de login exitoso
     */
    private void publishLoginSuccessEvent(String commandId, String username, String token) {
        EventMessage event = new EventMessage();
        event.setEventType("USER_AUTHENTICATED");
        event.setSource("auth-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", username);
        data.put("token", token);
        data.put("timestamp", System.currentTimeMillis());
        data.put("requested_resource", "dashboard");
        
        event.setData(data);
        
        publishEvent("auth.events", event);
        log.info("✅ Login success event published");
    }

    /**
     * ✅ Publicar evento de login fallido
     */
    private void publishLoginFailureEvent(String commandId, String username) {
        EventMessage event = new EventMessage();
        event.setEventType("AUTHENTICATION_FAILED");
        event.setSource("auth-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", username);
        data.put("reason", "Invalid credentials");
        data.put("timestamp", System.currentTimeMillis());
        
        event.setData(data);
        
        publishEvent("auth.events", event);
        log.warn("❌ Login failure event published");
    }

    /**
     * ✅ Publicar evento de validación de token
     */
    private void publishTokenValidEvent(String commandId, String token, String username, boolean isValid) {
        EventMessage event = new EventMessage();
        event.setEventType("TOKEN_VALIDATED");
        event.setSource("auth-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", username);
        data.put("valid", isValid);
        data.put("timestamp", System.currentTimeMillis());
        
        event.setData(data);
        
        publishEvent("auth.events", event);
    }

    /**
     * ✅ Publicar evento de token refrescado
     */
    private void publishTokenRefreshedEvent(String commandId, String username, String newToken) {
        EventMessage event = new EventMessage();
        event.setEventType("TOKEN_REFRESHED");
        event.setSource("auth-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("new_token", newToken);
        data.put("timestamp", System.currentTimeMillis());
        
        event.setData(data);
        
        publishEvent("auth.events", event);
    }

    /**
     * ✅ Inicializar usuarios de prueba
     */
    private void initializeUsers() {
        Map<String, String> users = new HashMap<>();
        users.put("admin", "Admin@Secure2024!");
        users.put("user1", "User@Secure2024!");
        users.put("security", "Security@Secure2024!");
        
        updateState("users", users);
        log.info("✅ Users initialized");
    }

    /**
     * ✅ Incrementar contador de logins exitosos
     */
    private void incrementSuccessfulLogins() {
        Long count = (Long) getState("logins_successful");
        if (count == null) count = 0L;
        updateState("logins_successful", count + 1);
    }

    /**
     * ✅ Incrementar contador de logins fallidos
     */
    private void incrementFailedLogins() {
        Long count = (Long) getState("logins_failed");
        if (count == null) count = 0L;
        updateState("logins_failed", count + 1);
    }
}

