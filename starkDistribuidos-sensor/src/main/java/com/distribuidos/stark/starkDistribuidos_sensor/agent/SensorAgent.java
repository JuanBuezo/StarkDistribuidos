package com.distribuidos.stark.starkDistribuidos_sensor.agent;

import com.distribuidos.stark.agent.ServiceAgent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ✅ SENSOR AGENT
 * 
 * Responsabilidades:
 * • Procesar comandos de lectura de sensores
 * • Simular lecturas de sensores
 * • Publicar datos de sensores a Kafka
 * • Monitorear salud de sensores
 * • Generar eventos periódicamente
 * 
 * Comunicación:
 * • Entrada: RabbitMQ (sensor.command.q)
 * • Salida: Kafka (sensor.data)
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Service
public class SensorAgent extends ServiceAgent {

    @Override
    protected void onInitialize() {
        log.info("📡 SensorAgent initialized");
        updateState("agent_type", "SENSOR");
        updateState("readings_published", 0L);
        updateState("sensors_active", 3);
    }

    /**
     * ✅ Escuchar comandos de sensor desde RabbitMQ
     */
    @RabbitListener(queues = "sensor.command.q")
    public void handleSensorCommand(String message) {
        try {
            CommandMessage command = objectMapper.readValue(message, CommandMessage.class);
            log.info("📡 SensorAgent: Received command: {}", command.getCommandType());
            handleCommand(command);
        } catch (Exception e) {
            log.error("Error deserializing command", e);
        }
    }

    /**
     * ✅ Procesar comando de sensor
     */
    @Override
    protected void processCommand(CommandMessage command) throws Exception {
        updateStatus(AgentStatus.PROCESSING);
        
        String commandType = command.getCommandType();
        Map<String, Object> payload = command.getPayload();

        switch (commandType) {
            case "READ_SENSOR":
                handleReadSensor(command.getCommandId(), payload);
                break;
            case "GET_ALL_SENSORS":
                handleGetAllSensors(command.getCommandId());
                break;
            case "CONFIGURE_SENSOR":
                handleConfigureSensor(command.getCommandId(), payload);
                break;
            default:
                log.warn("Unknown command type: {}", commandType);
        }
        
        updateStatus(AgentStatus.READY);
    }

    /**
     * ✅ Procesar evento (si es necesario)
     */
    @Override
    protected void processEvent(EventMessage event) throws Exception {
        // SensorAgent puede escuchar eventos de otros servicios
        log.debug("SensorAgent received event: {}", event.getEventType());
    }

    /**
     * ✅ Generar lecturas de sensores periódicamente
     */
    @Scheduled(fixedRate = 10000) // Cada 10 segundos
    public void generateSensorReadings() {
        if (status != AgentStatus.READY) {
            return;
        }

        log.info("📊 Generating sensor readings...");
        
        // Simular 3 sensores
        for (int i = 1; i <= 3; i++) {
            double value = generateRandomValue();
            publishSensorReading("sensor_" + i, value);
        }
        
        incrementReadingsPublished();
    }

    /**
     * ✅ Generar valor aleatorio (0-100)
     */
    private double generateRandomValue() {
        Random random = new Random();
        return random.nextDouble() * 100;
    }

    /**
     * ✅ Manejar comando READ_SENSOR
     */
    private void handleReadSensor(String commandId, Map<String, Object> payload) {
        String sensorId = (String) payload.get("sensor_id");
        
        log.info("📡 Reading sensor: {}", sensorId);
        
        double value = generateRandomValue();
        publishSensorReading(sensorId, value);
    }

    /**
     * ✅ Manejar comando GET_ALL_SENSORS
     */
    private void handleGetAllSensors(String commandId) {
        log.info("📡 Getting all sensors");
        
        EventMessage event = new EventMessage();
        event.setEventType("SENSORS_LIST");
        event.setSource("sensor-service");
        
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sensors = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> sensor = new HashMap<>();
            sensor.put("sensor_id", "sensor_" + i);
            sensor.put("status", "ACTIVE");
            sensor.put("last_reading", generateRandomValue());
            sensors.add(sensor);
        }
        
        data.put("sensors", sensors);
        data.put("total_sensors", 3);
        event.setData(data);
        
        publishEvent("sensor.data", event);
    }

    /**
     * ✅ Manejar comando CONFIGURE_SENSOR
     */
    private void handleConfigureSensor(String commandId, Map<String, Object> payload) {
        String sensorId = (String) payload.get("sensor_id");
        String parameter = (String) payload.get("parameter");
        Object value = payload.get("value");
        
        log.info("⚙️  Configuring sensor: {} - {} = {}", sensorId, parameter, value);
        
        EventMessage event = new EventMessage();
        event.setEventType("SENSOR_CONFIGURED");
        event.setSource("sensor-service");
        event.setData(payload);
        
        publishEvent("sensor.data", event);
    }

    /**
     * ✅ Publicar lectura de sensor
     */
    private void publishSensorReading(String sensorId, double value) {
        EventMessage event = new EventMessage();
        event.setEventType("SENSOR_READING");
        event.setSource("sensor-service");
        
        Map<String, Object> data = new HashMap<>();
        data.put("sensor_id", sensorId);
        data.put("value", value);
        data.put("unit", "celsius");
        data.put("timestamp", System.currentTimeMillis());
        data.put("quality", "GOOD");
        
        event.setData(data);
        
        publishEvent("sensor.data", event);
        log.debug("📊 Sensor reading published: {} = {}", sensorId, value);
    }

    /**
     * ✅ Incrementar contador de lecturas
     */
    private void incrementReadingsPublished() {
        Long count = (Long) getState("readings_published");
        if (count == null) count = 0L;
        updateState("readings_published", count + 1);
    }
}

