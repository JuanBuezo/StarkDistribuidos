package com.distribuidos.stark.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    private final List<Map<String, Object>> alerts = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<?>> getAllAlerts() {
        return ResponseEntity.ok(alerts);
    }

    @PostMapping
    public ResponseEntity<?> createAlert(@RequestBody Map<String, Object> alert) {

        alert.put("timestamp", java.time.LocalDateTime.now());
        alert.put("level", alert.getOrDefault("level", "INFO"));

        alerts.add(alert);

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ALERT");
        notification.put("title", "New security alert");
        notification.put("message", alert.getOrDefault("message", "Security alert generated"));
        notification.put("recipient", alert.getOrDefault("recipient", "tony"));
        notification.put("email", alert.getOrDefault("email", ""));
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

        return ResponseEntity.ok(alert);
    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateAlert() {
        List<String> levels = Arrays.asList("CRITICAL", "WARNING", "INFO");
        List<String> messages = Arrays.asList(
                "Movimiento detectado en entrada principal",
                "Acceso sospechoso detectado",
                "Sensor activado fuera de horario",
                "Intento de acceso no autorizado"
        );

        Map<String, Object> alert = new HashMap<>();
        alert.put("message", messages.get(random.nextInt(messages.size())));
        alert.put("level", levels.get(random.nextInt(levels.size())));
        alert.put("recipient", "tony");
        alert.put("email", "");
        alert.put("timestamp", java.time.LocalDateTime.now());

        alerts.add(alert);
        createNotification(alert);

        return ResponseEntity.ok(alert);
    }

    @DeleteMapping
    public ResponseEntity<?> clearAlerts() {
        alerts.clear();
        return ResponseEntity.ok().build();
    }

    private void createNotification(Map<String, Object> alert) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ALERT");
        notification.put("title", "New security alert");
        notification.put("message", alert.getOrDefault("message", "Security alert generated"));
        notification.put("recipient", alert.getOrDefault("recipient", "tony"));
        notification.put("email", alert.getOrDefault("email", ""));
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