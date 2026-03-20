package com.distribuidos.stark.controller;

import com.distribuidos.stark.entity.AccessLog;
import com.distribuidos.stark.service.AccessControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para control de acceso y auditoría.
 */
@RestController
@RequestMapping("/stark-security/api/access")
@RequiredArgsConstructor
@Slf4j
public class AccessController {
    
    private final AccessControlService accessControlService;
    
    @PostMapping("/log")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Void> logAccessAttempt(
            @RequestParam Long sensorId,
            @RequestParam String username,
            @RequestParam Boolean granted,
            @RequestParam(required = false) String ipAddress) {
        log.info("POST /access/log - Registrando intento de acceso");
        accessControlService.logAccessAttempt(sensorId, username, granted, ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @PostMapping("/log-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Void> logFailedAttempt(
            @RequestParam Long sensorId,
            @RequestParam String username,
            @RequestParam(required = false) String ipAddress,
            @RequestParam String reason) {
        log.info("POST /access/log-failed - Registrando intento fallido");
        accessControlService.logFailedAttempt(sensorId, username, ipAddress, reason);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/logs/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Page<AccessLog>> getAccessLogs(
            @PathVariable String username,
            Pageable pageable) {
        log.info("GET /access/logs/{} - Obteniendo logs de acceso", username);
        Page<AccessLog> logs = accessControlService.getAccessLogs(username, pageable);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/logs/sensor/{sensorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Page<AccessLog>> getAccessLogsBySensor(
            @PathVariable Long sensorId,
            Pageable pageable) {
        log.info("GET /access/logs/sensor/{} - Obteniendo logs del sensor", sensorId);
        try {
            Page<AccessLog> logs = accessControlService.getAccessLogsBySensor(sensorId, pageable);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            log.error("Error al obtener logs: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/denied")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<AccessLog>> getDeniedAccess(
            @RequestParam(required = false) Integer hoursAgo) {
        log.info("GET /access/denied - Obteniendo accesos denegados");
        LocalDateTime since = hoursAgo != null
            ? LocalDateTime.now().minusHours(hoursAgo)
            : LocalDateTime.now().minusDays(1);
        List<AccessLog> logs = accessControlService.getDeniedAccess(since);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/user/{username}/failed-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<FailedAccessCount> getFailedAccessCount(@PathVariable String username) {
        log.info("GET /access/user/{}/failed-count", username);
        long count = accessControlService.getFailedAccessCount(username);
        return ResponseEntity.ok(new FailedAccessCount(count));
    }
    
    @GetMapping("/user/{username}/recent-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<AccessLog>> getRecentFailedAccess(@PathVariable String username) {
        log.info("GET /access/user/{}/recent-failed", username);
        List<AccessLog> logs = accessControlService.getRecentFailedAccess(username);
        return ResponseEntity.ok(logs);
    }
    
    @PostMapping("/audit/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> auditUserAccess(@PathVariable String username) {
        log.info("POST /access/audit/{} - Realizando auditoría de usuario", username);
        accessControlService.auditUserAccess(username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateSecurityReport() {
        log.info("GET /access/report - Generando reporte de seguridad");
        accessControlService.generateSecurityReport();
        return ResponseEntity.ok("Reporte de seguridad generado");
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class FailedAccessCount {
        private long count;
    }
}

