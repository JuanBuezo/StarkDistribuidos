package com.distribuidos.stark.starkDistribuidos_notification.agent;

import com.distribuidos.stark.agent.ServiceAgent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ✅ NOTIFICATION AGENT
 * 
 * Responsabilidades:
 * • Procesar alertas de otros servicios
 * • Enviar notificaciones (email, SMS)
 * • Registrar historial de notificaciones
 * • Manejar reintentos de envío
 * • Publicar eventos de confirmación
 * 
 * Comunicación:
 * • Entrada: Kafka (alert.triggered)
 * • Salida: Kafka (notification.sent)
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class NotificationAgent extends ServiceAgent {

    @Override
    protected void onInitialize() {
        log.info("📧 NotificationAgent initialized");
        updateState("agent_type", "NOTIFICATION");
        updateState("notifications_sent", 0L);
        updateState("notifications_failed", 0L);
    }

    /**
     * ✅ Escuchar alertas desde Kafka
     */
    @KafkaListener(topics = "alert.triggered", groupId = "notification-service")
    public void handleAlert(String message) {
        try {
            EventMessage event = objectMapper.readValue(message, EventMessage.class);
            log.info("🚨 NotificationAgent: Received alert: {}", event.getEventType());
            handleEvent(event);
        } catch (Exception e) {
            log.error("Error deserializing alert event", e);
        }
    }

    /**
     * ✅ Escuchar eventos de acceso denegado
     */
    @KafkaListener(topics = "access.events", groupId = "notification-service")
    public void handleAccessEvent(String message) {
        try {
            EventMessage event = objectMapper.readValue(message, EventMessage.class);
            
            if ("ACCESS_DENIED".equals(event.getEventType())) {
                log.warn("🚨 NotificationAgent: Access denied event received");
                handleEvent(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing access event", e);
        }
    }

    /**
     * ✅ Procesar comando de notificación (no usado normalmente)
     */
    @Override
    protected void processCommand(CommandMessage command) throws Exception {
        log.warn("NotificationAgent: Received command (unexpected): {}", command.getCommandType());
    }

    /**
     * ✅ Procesar evento de alerta
     */
    @Override
    protected void processEvent(EventMessage event) throws Exception {
        updateStatus(AgentStatus.PROCESSING);
        
        String eventType = event.getEventType();
        Map<String, Object> data = event.getData();
        
        if ("ALERT_TRIGGERED".equals(eventType)) {
            handleAlertNotification(event.getEventId(), data);
        } else if ("ACCESS_DENIED".equals(eventType)) {
            handleAccessDeniedNotification(event.getEventId(), data);
        }
        
        updateStatus(AgentStatus.READY);
    }

    /**
     * ✅ Manejar notificación de alerta
     */
    private void handleAlertNotification(String eventId, Map<String, Object> alertData) {
        String sensorId = (String) alertData.get("sensor_id");
        Object valueObj = alertData.get("value");
        String severity = (String) alertData.get("severity");
        
        log.info("📧 Sending alert notification - Sensor: {}, Severity: {}", sensorId, severity);
        
        String message;
        if ("CRITICAL".equals(severity)) {
            message = String.format("🚨 CRITICAL ALERT! Sensor %s reported critical value: %s", 
                sensorId, valueObj);
        } else {
            message = String.format("⚠️  Anomaly detected! Sensor %s value: %s", 
                sensorId, valueObj);
        }
        
        // Simular envío de email
        sendEmail("admin@starkindustries.com", "Security Alert", message);
        
        // Publicar evento de confirmación
        publishNotificationSentEvent(eventId, sensorId, true);
        incrementNotificationsSent();
    }

    /**
     * ✅ Manejar notificación de acceso denegado
     */
    private void handleAccessDeniedNotification(String eventId, Map<String, Object> accessData) {
        String userId = (String) accessData.get("user_id");
        String resource = (String) accessData.get("resource");
        
        log.warn("📧 Sending access denied notification - User: {}, Resource: {}", userId, resource);
        
        String message = String.format("❌ Access Denied! User %s attempted to access %s", 
            userId, resource);
        
        // Simular envío de email
        sendEmail("admin@starkindustries.com", "Access Denied Alert", message);
        
        // Publicar evento de confirmación
        publishNotificationSentEvent(eventId, userId, true);
        incrementNotificationsSent();
    }

    /**
     * ✅ Simular envío de email
     */
    private void sendEmail(String recipient, String subject, String message) {
        log.info("📧 Sending email:");
        log.info("   To: {}", recipient);
        log.info("   Subject: {}", subject);
        log.info("   Message: {}", message);
        
        // TODO: Integrar con servicio de email real (SendGrid, AWS SES, etc.)
        
        log.info("✅ Email sent successfully");
    }

    /**
     * ✅ Publicar evento de notificación enviada
     */
    private void publishNotificationSentEvent(String sourceEventId, String recipient, boolean success) {
        EventMessage event = new EventMessage();
        event.setEventType("NOTIFICATION_SENT");
        event.setSource("notification-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("source_event_id", sourceEventId);
        data.put("recipient", recipient);
        data.put("status", success ? "SENT" : "FAILED");
        data.put("sent_at", System.currentTimeMillis());
        
        event.setData(data);
        
        publishEvent("notification.sent", event);
        log.info("✅ Notification sent event published");
    }

    /**
     * ✅ Incrementar contador de notificaciones enviadas
     */
    private void incrementNotificationsSent() {
        Long count = (Long) getState("notifications_sent");
        if (count == null) count = 0L;
        updateState("notifications_sent", count + 1);
    }

    /**
     * ✅ Incrementar contador de fallos
     */
    private void incrementNotificationsFailed() {
        Long count = (Long) getState("notifications_failed");
        if (count == null) count = 0L;
        updateState("notifications_failed", count + 1);
    }
}

