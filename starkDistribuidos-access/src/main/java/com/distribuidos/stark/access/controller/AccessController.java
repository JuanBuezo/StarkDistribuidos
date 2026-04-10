package com.distribuidos.stark.access.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@CrossOrigin(origins = "*")
public class AccessController {
    @GetMapping
    public ResponseEntity<List<?>> getAllAccessLogs() {
        return ResponseEntity.ok(Arrays.asList());
    }
    @PostMapping
    public ResponseEntity<?> logAccess(@RequestBody Object log) {
        return ResponseEntity.ok(log);
    }
}

