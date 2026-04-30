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

        Alert savedAlert = alertRepository.save(alert);

        // Crear notificación
        createNotification(savedAlert);

        return ResponseEntity.ok(savedAlert);
    }

    @PostMapping("/simulate")
    public ResponseEntity<Alert> simulateAlert() {
        List<String> levels = Arrays.asList("CRITICAL", "WARNING", "INFO");
        List<String> messages = Arrays.asList(
                "Movimiento detectado en entrada principal",
                "Acceso sospechoso detectado",
                "Sensor activado fuera de horario",
                "Intento de acceso no autorizado"
        );

        Alert alert = new Alert();
        alert.message = messages.get(random.nextInt(messages.size()));
        alert.level = levels.get(random.nextInt(levels.size()));
        alert.recipient = "tony";
        alert.email = "";

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