package com.distribuidos.stark.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.distribuidos.stark.alert.model.Alert;
import com.distribuidos.stark.alert.repository.AlertRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Autowired
    private AlertRepository alertRepository;

    // Definición de sensores disponibles para simulación
    private static final List<Map<String, Object>> AVAILABLE_SENSORS = Arrays.asList(
        Map.of("id", 1L, "name", "Sensor Movimiento Entrada", "type", "MOTION"),
        Map.of("id", 2L, "name", "Sensor Intrusión Laboratorio", "type", "INTRUSION"),
        Map.of("id", 3L, "name", "Sensor Humo Sala Servidores", "type", "SMOKE"),
        Map.of("id", 4L, "name", "Sensor CO2 Oficina", "type", "CO2"),
        Map.of("id", 5L, "name", "Sensor Temperatura Data Center", "type", "TEMPERATURE"),
        Map.of("id", 6L, "name", "Sensor Humedad Almacén", "type", "HUMIDITY")
    );

    // Reglas de alertas por tipo de sensor
    private static final Map<String, List<Map<String, Object>>> ALERT_RULES = Map.of(
        "INTRUSION", Arrays.asList(
            Map.of("level", "CRITICAL", "message", "Intrusión detectada en {sensorName}"),
            Map.of("level", "WARNING", "message", "Actividad sospechosa en {sensorName}")
        ),
        "SMOKE", Arrays.asList(
            Map.of("level", "CRITICAL", "message", "Alto nivel de humo detectado en {sensorName}"),
            Map.of("level", "WARNING", "message", "Nivel de humo elevado en {sensorName}")
        ),
        "CO2", Arrays.asList(
            Map.of("level", "WARNING", "message", "Nivel de CO2 alto en {sensorName}"),
            Map.of("level", "INFO", "message", "Monitoreo de CO2 en {sensorName}")
        ),
        "MOTION", Arrays.asList(
            Map.of("level", "WARNING", "message", "Movimiento detectado fuera de horario en {sensorName}"),
            Map.of("level", "INFO", "message", "Movimiento registrado en {sensorName}")
        ),
        "TEMPERATURE", Arrays.asList(
            Map.of("level", "WARNING", "message", "Temperatura elevada detectada en {sensorName}"),
            Map.of("level", "INFO", "message", "Monitoreo de temperatura en {sensorName}")
        ),
        "HUMIDITY", Arrays.asList(
            Map.of("level", "WARNING", "message", "Humedad extrema detectada en {sensorName}"),
            Map.of("level", "INFO", "message", "Monitoreo de humedad en {sensorName}")
        )
    );

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertRepository.findByHiddenFalse();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Alert>> getAlertHistory() {
        List<Alert> alerts = alertRepository.findAll();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody Map<String, Object> alertData) {
        Alert alert = new Alert();
        alert.level = (String) alertData.getOrDefault("level", "INFO");
        alert.message = (String) alertData.get("message");
        alert.recipient = (String) alertData.get("recipient");
        alert.email = (String) alertData.get("email");

        // Si se proporciona información del sensor, usarla
        if (alertData.containsKey("sensorId")) {
            alert.sensorId = Long.valueOf(alertData.get("sensorId").toString());
        }
        if (alertData.containsKey("sensorName")) {
            alert.sensorName = (String) alertData.get("sensorName");
        }
        if (alertData.containsKey("sensorType")) {
            alert.sensorType = (String) alertData.get("sensorType");
        }

        Alert savedAlert = alertRepository.save(alert);
        createNotification(savedAlert);

        return ResponseEntity.ok(savedAlert);
    }

    @PostMapping("/simulate")
    public ResponseEntity<Alert> simulateAlert() {
        // Seleccionar sensor aleatorio
        Map<String, Object> sensor = AVAILABLE_SENSORS.get(random.nextInt(AVAILABLE_SENSORS.size()));

        // Obtener reglas para este tipo de sensor
        List<Map<String, Object>> sensorRules = ALERT_RULES.get(sensor.get("type"));
        if (sensorRules == null || sensorRules.isEmpty()) {
            // Fallback si no hay reglas definidas
            sensorRules = Arrays.asList(Map.of("level", "INFO", "message", "Alerta general del sistema"));
        }

        // Seleccionar regla aleatoria
        Map<String, Object> selectedRule = sensorRules.get(random.nextInt(sensorRules.size()));

        Alert alert = new Alert();
        alert.level = (String) selectedRule.get("level");
        alert.message = ((String) selectedRule.get("message")).replace("{sensorName}", (String) sensor.get("name"));
        alert.recipient = "tony";
        alert.email = "";

        // Asignar información del sensor
        alert.sensorId = (Long) sensor.get("id");
        alert.sensorName = (String) sensor.get("name");
        alert.sensorType = (String) sensor.get("type");

        Alert savedAlert = alertRepository.save(alert);
        createNotification(savedAlert);

        return ResponseEntity.ok(savedAlert);
    }

    @DeleteMapping
    public ResponseEntity<?> clearAlerts() {
        // Soft delete: marcar como hidden en lugar de borrar
        List<Alert> visibleAlerts = alertRepository.findByHiddenFalse();
        visibleAlerts.forEach(alert -> {
            alert.hidden = true;
            alertRepository.save(alert);
        });

        return ResponseEntity.ok(Collections.singletonMap("message", "Alertas ocultadas exitosamente"));
    }

    private void createNotification(Alert alert) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ALERT");
        notification.put("title", "New security alert");
        notification.put("message", alert.message);
        notification.put("recipient", alert.recipient);
        notification.put("email", alert.email);
        notification.put("read", false);

        try {
            restTemplate.postForObject(
                    "http://stark-notification:8086/api/notifications/email",
                    notification,
                    Object.class
            );
        } catch (Exception e) {
            System.out.println("Could not create notification: " + e.getMessage());
        }
    }
}