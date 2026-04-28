package com.distribuidos.stark.controller;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de alertas.
 */
@RestController
@RequestMapping("/stark-security/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final AlertService alertService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/unacknowledged")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Alert>> getUnacknowledgedAlerts() {
        List<Alert> alerts = alertService.getUnacknowledgedAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/critical")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Alert>> getCriticalAlerts() {
        List<Alert> alerts = alertService.getCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/level/{level}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Alert>> getAlertsByLevel(@PathVariable String level) {
        try {
            Alert.AlertLevel alertLevel = Alert.AlertLevel.valueOf(level.toUpperCase());
            List<Alert> alerts = alertService.getAlertsByLevel(alertLevel);
            return ResponseEntity.ok(alerts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/sensor/{sensorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Page<Alert>> getAlertsBySensor(
            @PathVariable Long sensorId,
            Pageable pageable) {
        return ResponseEntity.ok(Page.empty());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Alert> createAlert(@Valid @RequestBody Alert alert) {
        Alert savedAlert = alertService.createAlert(alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAlert);
    }
    
    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Alert> acknowledgeAlert(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "SYSTEM";
            Alert updated = alertService.acknowledgeAlert(id, username);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/dismiss-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> dismissAllAlerts(
            @RequestParam(defaultValue = "WARNING") String level) {
        try {
            alertService.dismissAllAlerts(level);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<AlertStats> getAlertStats() {
        AlertStats stats = AlertStats.builder()
            .unacknowledgedCount(alertService.getUnacknowledgedCount())
            .criticalCount(alertService.getCriticalUnacknowledgedCount())
            .build();
        return ResponseEntity.ok(stats);
    }
    
    @lombok.Data
    @lombok.Builder
    public static class AlertStats {
        private long unacknowledgedCount;
        private long criticalCount;
    }
}




