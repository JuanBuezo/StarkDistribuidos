package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.AccessLog;
import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.repository.AccessLogRepository;
import com.distribuidos.stark.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Servicio para control de acceso y auditoría.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccessControlService {
    
    private final AccessLogRepository accessLogRepository;
    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    
    private static final int FAILED_ATTEMPTS_THRESHOLD = 5;
    private static final int LOCKOUT_MINUTES = 15;
    
    @Transactional
    @Async("alertExecutor")
    public void logAccessAttempt(Long sensorId, String username, boolean granted, String ipAddress) {
        log.info("Registrando intento de acceso - Usuario: {}, Sensor: {}, Resultado: {}",
            username, sensorId, granted ? "PERMITIDO" : "DENEGADO");
        
        sensorRepository.findById(sensorId).ifPresent(sensor -> {
            AccessLog log = AccessLog.builder()
                .sensor(sensor)
                .username(username)
                .status(granted ? AccessLog.AccessStatus.GRANTED : AccessLog.AccessStatus.DENIED)
                .ipAddress(ipAddress)
                .details("Intento de acceso " + (granted ? "exitoso" : "fallido"))
                .build();
            
            accessLogRepository.save(log);
            
            // Evaluar si hay múltiples intentos fallidos
            if (!granted) {
                evaluateFailedAttempts(username, sensor);
            }
            
            // Notificar si fue denegado
            if (!granted) {
                notifyAccessDenial(username, sensor, ipAddress);
            }
        });
    }
    
    @Transactional
    @Async("alertExecutor")
    public void logFailedAttempt(Long sensorId, String username, String ipAddress, String reason) {
        log.warn("Registrando intento fallido - Usuario: {}, Sensor: {}, Razón: {}",
            username, sensorId, reason);
        
        sensorRepository.findById(sensorId).ifPresent(sensor -> {
            AccessLog log = AccessLog.builder()
                .sensor(sensor)
                .username(username)
                .status(AccessLog.AccessStatus.FAILED)
                .ipAddress(ipAddress)
                .details("Intento fallido - " + reason)
                .build();
            
            accessLogRepository.save(log);
            
            // Evaluar umbral de fallos
            evaluateFailedAttempts(username, sensor);
        });
    }
    
    private void evaluateFailedAttempts(String username, Sensor sensor) {
        LocalDateTime since = LocalDateTime.now().minus(LOCKOUT_MINUTES, ChronoUnit.MINUTES);
        List<AccessLog> recentDenied = accessLogRepository
            .findRecentDeniedAccessForUser(username, since);
        
        if (recentDenied.size() >= FAILED_ATTEMPTS_THRESHOLD) {
            log.error("ALERTA DE SEGURIDAD: Usuario {} ha excedido intentos fallidos", username);
            emailService.sendAccessDenialNotification(
                username,
                sensor.getName(),
                LocalDateTime.now().toString()
            );
        }
    }
    
    @Transactional
    private void notifyAccessDenial(String username, Sensor sensor, String ipAddress) {
        notificationService.sendToUser(
            username,
            "Tu intento de acceso a " + sensor.getLocation() + " fue denegado"
        );
    }
    
    public Page<AccessLog> getAccessLogs(String username, Pageable pageable) {
        log.info("Obteniendo logs de acceso para usuario: {}", username);
        return accessLogRepository.findAccessLogsByUsername(username, pageable);
    }
    
    public Page<AccessLog> getAccessLogsBySensor(Long sensorId, Pageable pageable) {
        log.info("Obteniendo logs de acceso para sensor: {}", sensorId);
        return sensorRepository.findById(sensorId)
            .map(sensor -> accessLogRepository.findAccessLogsBySensor(sensor, pageable))
            .orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
    }
    
    public List<AccessLog> getDeniedAccess(LocalDateTime since) {
        log.info("Obteniendo accesos denegados desde: {}", since);
        return accessLogRepository.findAccessLogsAfter(since).stream()
            .filter(log -> log.getStatus() == AccessLog.AccessStatus.DENIED)
            .toList();
    }
    
    public long getFailedAccessCount(String username) {
        log.debug("Contando accesos fallidos para usuario: {}", username);
        return accessLogRepository.countDeniedAccessForUser(username);
    }
    
    public List<AccessLog> getRecentFailedAccess(String username) {
        LocalDateTime since = LocalDateTime.now().minus(LOCKOUT_MINUTES, ChronoUnit.MINUTES);
        log.info("Obteniendo accesos fallidos recientes para usuario: {}", username);
        return accessLogRepository.findRecentDeniedAccessForUser(username, since);
    }
    
    @Transactional
    @Async("alertExecutor")
    public void auditUserAccess(String username) {
        log.info("Realizando auditoría de acceso para usuario: {}", username);
        
        LocalDateTime last24Hours = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        List<AccessLog> logs = accessLogRepository.findAccessLogsAfter(last24Hours).stream()
            .filter(log -> log.getUsername().equals(username))
            .toList();
        
        log.debug("Usuario {} tiene {} accesos en últimas 24 horas", username, logs.size());
    }
    
    @Transactional
    @Async("alertExecutor")
    public void generateSecurityReport() {
        log.info("Generando reporte de seguridad");
        
        LocalDateTime last24Hours = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        List<AccessLog> allLogs = accessLogRepository.findAccessLogsAfter(last24Hours);
        
        long deniedCount = allLogs.stream()
            .filter(log -> log.getStatus() == AccessLog.AccessStatus.DENIED)
            .count();
        
        long failedCount = allLogs.stream()
            .filter(log -> log.getStatus() == AccessLog.AccessStatus.FAILED)
            .count();
        
        StringBuilder report = new StringBuilder();
        report.append("REPORTE DE SEGURIDAD - ÚLTIMAS 24 HORAS\n");
        report.append("=====================================\n");
        report.append("Total de intentos de acceso: ").append(allLogs.size()).append("\n");
        report.append("Accesos denegados: ").append(deniedCount).append("\n");
        report.append("Intentos fallidos: ").append(failedCount).append("\n");
        report.append("=====================================\n");
        
        log.info(report.toString());
    }
}

