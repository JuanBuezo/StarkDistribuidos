package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.entity.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.distribuidos.stark.entity.Notification;
import com.distribuidos.stark.repository.NotificationRepository;
import java.time.LocalDateTime;

/**
 * Servicio de notificaciones en tiempo real usando WebSocket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    @Async("alertExecutor")
    public void publishAlert(Alert alert) {
        log.debug("Publicando alerta por WebSocket: {}", alert.getId());

        try {
            messagingTemplate.convertAndSend(
                    "/topic/alerts",
                    buildAlertMessage(alert)
            );

            saveNotification("ALERT", "/topic/alerts", alert.getMessage(), "SENT");

        } catch (Exception e) {
            saveNotification("ALERT", "/topic/alerts", alert.getMessage(), "ERROR");
            log.error("Error al publicar alerta", e);
        }
    }

    @Async("alertExecutor")
    public void publishAlertAcknowledged(Alert alert) {
        log.debug("Publicando reconocimiento de alerta: {}", alert.getId());

        try {
            messagingTemplate.convertAndSend(
                    "/topic/alerts/acknowledged",
                    buildAlertMessage(alert)
            );

            saveNotification("ALERT_ACK", "/topic/alerts/acknowledged", alert.getMessage(), "SENT");

        } catch (Exception e) {
            saveNotification("ALERT_ACK", "/topic/alerts/acknowledged", alert.getMessage(), "ERROR");
            log.error("Error al publicar reconocimiento", e);
        }
    }
    
    @Async("sensorExecutor")
    public void publishSensorUpdate(Sensor sensor) {
        log.debug("Publicando actualización de sensor: {}", sensor.getId());
        
        try {
            messagingTemplate.convertAndSend(
                "/topic/sensor-data",
                buildSensorMessage(sensor)
            );
            
            messagingTemplate.convertAndSend(
                "/topic/sensor/" + sensor.getId(),
                buildSensorMessage(sensor)
            );
        } catch (Exception e) {
            log.error("Error al publicar actualización de sensor", e);
        }
    }
    
    @Async("alertExecutor")
    public void publishSystemStatus(String status) {
        log.debug("Publicando estado del sistema: {}", status);
        
        try {
            messagingTemplate.convertAndSend(
                "/topic/system-status",
                status
            );
        } catch (Exception e) {
            log.error("Error al publicar estado del sistema", e);
        }
    }
    
    @Async("alertExecutor")
    public void sendToUser(String username, String message) {
        log.debug("Enviando mensaje a usuario: {}", username);
        
        try {
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/messages",
                message
            );
        } catch (Exception e) {
            log.error("Error al enviar mensaje al usuario", e);
        }
    }
    
    @Async("alertExecutor")
    public void broadcastMessage(String destination, Object message) {
        log.debug("Broadcast a: {}", destination);
        
        try {
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("Error al hacer broadcast", e);
        }
    }
    
    private AlertMessage buildAlertMessage(Alert alert) {
        return AlertMessage.builder()
            .id(alert.getId())
            .sensorId(alert.getSensor().getId())
            .sensorName(alert.getSensor().getName())
            .level(alert.getLevel().toString())
            .message(alert.getMessage())
            .details(alert.getDetails())
            .acknowledged(alert.getAcknowledged())
            .timestamp(alert.getCreatedAt())
            .build();
    }
    
    private SensorMessage buildSensorMessage(Sensor sensor) {
        return SensorMessage.builder()
            .id(sensor.getId())
            .name(sensor.getName())
            .type(sensor.getType().toString())
            .location(sensor.getLocation())
            .active(sensor.getActive())
            .lastValue(sensor.getLastValue())
            .lastStatus(sensor.getLastStatus())
            .failureCount(sensor.getFailureCount())
            .timestamp(sensor.getUpdatedAt())
            .build();
    }

    private void saveNotification(String type, String destination, String message, String status) {
        notificationRepository.save(Notification.builder()
                .type(type)
                .destination(destination)
                .message(message)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build());
    }
    
    // DTOs para WebSocket
    @lombok.Data
    @lombok.Builder
    public static class AlertMessage {
        private Long id;
        private Long sensorId;
        private String sensorName;
        private String level;
        private String message;
        private String details;
        private Boolean acknowledged;
        private java.time.LocalDateTime timestamp;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SensorMessage {
        private Long id;
        private String name;
        private String type;
        private String location;
        private Boolean active;
        private Float lastValue;
        private String lastStatus;
        private Integer failureCount;
        private java.time.LocalDateTime timestamp;
    }
}

