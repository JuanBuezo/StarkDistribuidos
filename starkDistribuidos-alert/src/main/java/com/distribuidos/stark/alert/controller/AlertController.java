package com.distribuidos.stark.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<List<?>> getAllAlerts() {
        return ResponseEntity.ok(Arrays.asList());
    }

    @PostMapping
    public ResponseEntity<?> createAlert(@RequestBody Map<String, Object> alert) {

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ALERT");
        notification.put("title", "New security alert");
        notification.put("message", alert.getOrDefault("message", "Security alert generated"));
        notification.put("recipient", alert.getOrDefault("recipient", "tony"));
        notification.put("read", false);

        try {
            restTemplate.postForObject(
                    "http://stark-notification:8086/api/notifications",
                    notification,
                    Object.class
            );
        } catch (Exception e) {
            System.out.println("Could not create notification: " + e.getMessage());
        }

        return ResponseEntity.ok(alert);
    }
}