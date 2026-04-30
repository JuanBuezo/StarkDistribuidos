package com.distribuidos.stark.access.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/access")
// @CrossOrigin(origins = "*")
public class AccessController {

    private final List<Map<String, Object>> accessLogs = new ArrayList<>();
    private final Random random = new Random();

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAccessLogs() {
        return ResponseEntity.ok(accessLogs);
    }

    @PostMapping
    public ResponseEntity<?> logAccess(@RequestBody Map<String, Object> log) {
        log.putIfAbsent("timestamp", LocalDateTime.now().toString());
        accessLogs.add(log);
        return ResponseEntity.ok(log);
    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateAccess() {
        List<String> users = Arrays.asList("tony", "admin", "operator", "guest");
        List<String> locations = Arrays.asList("Entrada Principal", "Laboratorio", "Sala Servidores", "Parking");

        Map<String, Object> log = new HashMap<>();
        log.put("username", users.get(random.nextInt(users.size())));
        log.put("sensorId", random.nextInt(5) + 1);
        log.put("location", locations.get(random.nextInt(locations.size())));
        log.put("granted", random.nextBoolean());
        log.put("ipAddress", "192.168.1." + (random.nextInt(200) + 10));
        log.put("timestamp", LocalDateTime.now().toString());

        accessLogs.add(log);

        return ResponseEntity.ok(log);
    }

    @DeleteMapping
    public ResponseEntity<?> clearAccessLogs() {
        accessLogs.clear();
        return ResponseEntity.ok().build();
    }
}