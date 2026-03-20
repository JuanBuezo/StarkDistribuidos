package com.distribuidos.stark.controller;

import com.distribuidos.stark.service.SensorService;
import com.distribuidos.stark.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST para información del sistema.
 */
@RestController
@RequestMapping("/stark-security/api/system")
@RequiredArgsConstructor
@Slf4j
public class SystemController {
    
    private final SensorService sensorService;
    private final AlertService alertService;
    
    @GetMapping("/status")
    public ResponseEntity<SystemStatus> getSystemStatus() {
        log.info("GET /system/status - Obteniendo estado del sistema");
        
        SystemStatus status = SystemStatus.builder()
            .timestamp(LocalDateTime.now())
            .status("OPERATIONAL")
            .activeSensors(sensorService.getActiveSensorCount())
            .unacknowledgedAlerts(alertService.getUnacknowledgedCount())
            .criticalAlerts(alertService.getCriticalUnacknowledgedCount())
            .uptime(System.currentTimeMillis())
            .build();
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> getHealth() {
        log.info("GET /system/health - Obteniendo salud del sistema");
        
        boolean isHealthy = sensorService.getActiveSensorCount() > 0;
        
        HealthResponse health = HealthResponse.builder()
            .status(isHealthy ? "UP" : "DOWN")
            .timestamp(LocalDateTime.now())
            .sensorsHealthy(true)
            .databaseHealthy(true)
            .websocketHealthy(true)
            .build();
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/info")
    public ResponseEntity<SystemInfo> getSystemInfo() {
        log.info("GET /system/info - Obteniendo información del sistema");
        
        SystemInfo info = SystemInfo.builder()
            .applicationName("Stark Industries Security System")
            .version("1.0.0")
            .description("Sistema de Seguridad Distribuido con monitoreo en tiempo real")
            .environment("development")
            .javaVersion(System.getProperty("java.version"))
            .osName(System.getProperty("os.name"))
            .osVersion(System.getProperty("os.version"))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<SystemMetrics> getSystemMetrics() {
        log.info("GET /system/metrics - Obteniendo métricas del sistema");
        
        Runtime runtime = Runtime.getRuntime();
        
        SystemMetrics metrics = SystemMetrics.builder()
            .timestamp(LocalDateTime.now())
            .memoryUsed(runtime.totalMemory() - runtime.freeMemory())
            .memoryMax(runtime.maxMemory())
            .processors(Runtime.getRuntime().availableProcessors())
            .activeSensors(sensorService.getActiveSensorCount())
            .totalAlerts(alertService.getAllAlerts().size())
            .unacknowledgedAlerts(alertService.getUnacknowledgedCount())
            .build();
        
        return ResponseEntity.ok(metrics);
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SystemStatus {
        private LocalDateTime timestamp;
        private String status;
        private long activeSensors;
        private long unacknowledgedAlerts;
        private long criticalAlerts;
        private long uptime;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class HealthResponse {
        private String status;
        private LocalDateTime timestamp;
        private boolean sensorsHealthy;
        private boolean databaseHealthy;
        private boolean websocketHealthy;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SystemInfo {
        private String applicationName;
        private String version;
        private String description;
        private String environment;
        private String javaVersion;
        private String osName;
        private String osVersion;
        private LocalDateTime timestamp;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SystemMetrics {
        private LocalDateTime timestamp;
        private long memoryUsed;
        private long memoryMax;
        private int processors;
        private long activeSensors;
        private long totalAlerts;
        private long unacknowledgedAlerts;
    }
}

