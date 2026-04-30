package com.distribuidos.stark.access.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.distribuidos.stark.access.model.AccessLog;
import com.distribuidos.stark.access.repository.AccessLogRepository;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/access")
// @CrossOrigin(origins = "*")
public class AccessController {

    private final Random random = new Random();

    @Autowired
    private AccessLogRepository accessLogRepository;

    @GetMapping
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        List<AccessLog> logs = accessLogRepository.findByHiddenFalse();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AccessLog>> getAccessHistory() {
        List<AccessLog> logs = accessLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<AccessLog> logAccess(@RequestBody Map<String, Object> logData) {
        AccessLog log = new AccessLog();
        log.username = (String) logData.get("username");
        log.sensorId = logData.get("sensorId") != null ? ((Number) logData.get("sensorId")).intValue() : null;
        log.location = (String) logData.get("location");
        log.granted = (Boolean) logData.get("granted");
        log.ipAddress = (String) logData.get("ipAddress");

        AccessLog savedLog = accessLogRepository.save(log);
        return ResponseEntity.ok(savedLog);
    }

    @PostMapping("/simulate")
    public ResponseEntity<AccessLog> simulateAccess() {
        List<String> users = Arrays.asList("tony", "admin", "operator", "guest");
        List<String> locations = Arrays.asList("Entrada Principal", "Laboratorio", "Sala Servidores", "Parking");

        AccessLog log = new AccessLog();
        log.username = users.get(random.nextInt(users.size()));
        log.sensorId = random.nextInt(5) + 1;
        log.location = locations.get(random.nextInt(locations.size()));
        log.granted = random.nextBoolean();
        log.ipAddress = "192.168.1." + (random.nextInt(200) + 10);

        AccessLog savedLog = accessLogRepository.save(log);
        return ResponseEntity.ok(savedLog);
    }

    @DeleteMapping
    public ResponseEntity<?> clearAccessLogs() {
        // Soft delete: marcar como hidden en lugar de borrar
        List<AccessLog> visibleLogs = accessLogRepository.findByHiddenFalse();
        visibleLogs.forEach(log -> {
            log.hidden = true;
            accessLogRepository.save(log);
        });

        return ResponseEntity.ok(Collections.singletonMap("message", "Logs de acceso ocultados exitosamente"));
    }
}