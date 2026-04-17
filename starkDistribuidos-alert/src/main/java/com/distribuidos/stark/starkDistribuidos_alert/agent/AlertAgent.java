package com.distribuidos.stark.starkDistribuidos_alert.agent;

import com.distribuidos.stark.agent.ServiceAgent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ✅ ALERT AGENT
 * 
 * Responsabilidades:
 * • Procesar eventos de sensores
 * • Detectar anomalías en datos
 * • Generar alertas automáticas
 * • Publicar eventos de alerta
 * • Escalar alertas críticas
 * 
 * Comunicación:
 * • Entrada: Kafka (sensor.data)
 * • Salida: Kafka (alert.triggered)
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class AlertAgent extends ServiceAgent {

    private static final double ALERT_THRESHOLD = 75.0;

    @Override
    protected void onInitialize() {
        log.info("🚨 AlertAgent initialized");
        updateState("agent_type", "ALERT");
        updateState("alerts_generated", 0L);
        updateState("anomalies_detected", 0L);
    }

    /**
     * ✅ Escuchar eventos de sensores desde Kafka
     */
    @KafkaListener(topics = "sensor.data", groupId = "alert-service")
    public void handleSensorData(String message) {
        try {
            EventMessage event = objectMapper.readValue(message, EventMessage.class);
            log.info("📊 AlertAgent: Received sensor data: {}", event.getEventType());
            handleEvent(event);
        } catch (Exception e) {
            log.error("Error deserializing sensor event", e);
        }
    }

    /**
     * ✅ Procesar evento de sensor
     */
    @Override
    protected void processCommand(CommandMessage command) throws Exception {
        // Alert no recibe comandos normalmente, solo eventos
        log.warn("AlertAgent: Received command (unexpected): {}", command.getCommandType());
    }

    /**
     * ✅ Procesar evento de sensor
     */
    @Override
    protected void processEvent(EventMessage event) throws Exception {
        updateStatus(AgentStatus.PROCESSING);
        
        String eventType = event.getEventType();
        
        if ("SENSOR_READING".equals(eventType)) {
            Map<String, Object> data = event.getData();
            analyzeSensorData(event.getEventId(), data);
        }
        
        updateStatus(AgentStatus.READY);
    }

    /**
     * ✅ Analizar datos de sensor y detectar anomalías
     */
    private void analyzeSensorData(String eventId, Map<String, Object> sensorData) {
        String sensorId = (String) sensorData.get("sensor_id");
        Object valueObj = sensorData.get("value");
        double value = 0.0;
        
        if (valueObj instanceof Number) {
            value = ((Number) valueObj).doubleValue();
        }
        
        log.info("📊 Analyzing sensor {} with value: {}", sensorId, value);
        
        // Detectar anomalías (valor > umbral)
        if (value > ALERT_THRESHOLD) {
            log.warn("⚠️  Anomaly detected! Sensor: {}, Value: {}", sensorId, value);
            
            publishAlert(eventId, sensorId, value, "ANOMALY");
            incrementAnomaliesDetected();
        }
        
        // Detectar valores críticos
        if (value > 90.0) {
            log.error("🔴 CRITICAL! Sensor: {}, Value: {}", sensorId, value);
            
            publishAlert(eventId, sensorId, value, "CRITICAL");
            incrementAlertsGenerated();
        }
    }

    /**
     * ✅ Publicar evento de alerta
     */
    private void publishAlert(String sourceEventId, String sensorId, double value, String severity) {
        EventMessage alert = new EventMessage();
        alert.setEventType("ALERT_TRIGGERED");
        alert.setSource("alert-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("source_event_id", sourceEventId);
        data.put("sensor_id", sensorId);
        data.put("value", value);
        data.put("severity", severity);
        data.put("threshold", ALERT_THRESHOLD);
        data.put("triggered_at", System.currentTimeMillis());
        
        alert.setData(data);
        
        publishEvent("alert.triggered", alert);
        log.info("🚨 Alert published: severity={}, sensor={}", severity, sensorId);
    }

    /**
     * ✅ Incrementar contador de alertas
     */
    private void incrementAlertsGenerated() {
        Long count = (Long) getState("alerts_generated");
        if (count == null) count = 0L;
        updateState("alerts_generated", count + 1);
    }

    /**
     * ✅ Incrementar contador de anomalías
     */
    private void incrementAnomaliesDetected() {
        Long count = (Long) getState("anomalies_detected");
        if (count == null) count = 0L;
        updateState("anomalies_detected", count + 1);
    }
}

