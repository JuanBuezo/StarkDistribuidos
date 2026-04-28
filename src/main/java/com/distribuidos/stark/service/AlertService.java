package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.distribuidos.stark.repository.SensorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de alertas.
 * Maneja la creación, actualización y notificación de alertas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AlertService {
    
    private final AlertRepository alertRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SensorRepository sensorRepository;
    
    public List<Alert> getAllAlerts() {
        log.info("Obteniendo todas las alertas");
        return alertRepository.findAll();
    }
    
    public List<Alert> getUnacknowledgedAlerts() {
        log.info("Obteniendo alertas no reconocidas");
        return alertRepository.findUnacknowledgedAlerts();
    }
    
    public List<Alert> getCriticalAlerts() {
        log.info("Obteniendo alertas críticas");
        return alertRepository.findCriticalUnacknowledgedAlerts();
    }
    
    public Optional<Alert> getAlertById(Long id) {
        log.info("Obteniendo alerta: {}", id);
        return alertRepository.findById(id);
    }
    
    public List<Alert> getAlertsByLevel(Alert.AlertLevel level) {
        log.info("Obteniendo alertas de nivel: {}", level);
        return alertRepository.findByLevelAndAcknowledgedFalse(level);
    }
    
    public Page<Alert> getAlertsBySensor(Sensor sensor, Pageable pageable) {
        log.info("Obteniendo alertas del sensor: {}", sensor.getId());
        return alertRepository.findAlertsBySensor(sensor, pageable);
    }
    
    public List<Alert> getAlertsAfter(LocalDateTime since) {
        log.info("Obteniendo alertas desde: {}", since);
        return alertRepository.findAlertsAfter(since);
    }
    
    @Transactional
    @Async("alertExecutor")
    public void createAlert(Sensor sensor, String level, String message) {
        log.warn("Creando alerta nivel {} para sensor: {}", level, sensor.getName());
        
        Alert alert = Alert.builder()
            .sensor(sensor)
            .level(Alert.AlertLevel.valueOf(level))
            .message(message)
            .details("Sensor: " + sensor.getName() + " | Ubicación: " + sensor.getLocation())
            .acknowledged(false)
            .build();
        
        Alert savedAlert = alertRepository.save(alert);
        log.info("Alerta creada con ID: {}", savedAlert.getId());
        
        // Notificar inmediatamente
        notifyAlert(savedAlert);
    }

    @Transactional
    public Alert createAlert(Alert alert) {
        log.warn("Creando alerta: {}", alert.getMessage());

        Long sensorId = alert.getSensor().getId();

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new RuntimeException("Sensor no encontrado: " + sensorId));

        alert.setSensor(sensor);

        Alert savedAlert = alertRepository.save(alert);
        log.info("Alerta creada con ID: {}", savedAlert.getId());

        notifyAlert(savedAlert);

        return savedAlert;
    }
    
    @Transactional
    @Async("alertExecutor")
    private void notifyAlert(Alert alert) {
        log.info("Notificando alerta: {}", alert.getId());
        
        // Enviar por WebSocket
        notificationService.publishAlert(alert);
        
        // Enviar por email si es crítico
        if (alert.getLevel() == Alert.AlertLevel.CRITICAL) {
            log.warn("Enviando email para alerta crítica: {}", alert.getId());
            emailService.sendCriticalAlert(alert);
        }
    }
    
    @Transactional
    public Alert acknowledgeAlert(Long alertId, String acknowledgedBy) {
        log.info("Reconociendo alerta: {}", alertId);
        
        return alertRepository.findById(alertId)
            .map(alert -> {
                alert.setAcknowledged(true);
                alert.setAcknowledgedAt(LocalDateTime.now());
                alert.setAcknowledgedBy(acknowledgedBy);
                
                Alert updated = alertRepository.save(alert);
                log.info("Alerta reconocida: {}", alertId);
                
                // Notificar que fue reconocida
                notificationService.publishAlertAcknowledged(updated);
                
                return updated;
            })
            .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertId));
    }
    
    @Transactional
    public void deleteAlert(Long id) {
        log.info("Eliminando alerta: {}", id);
        alertRepository.deleteById(id);
    }
    
    @Transactional
    @Async("alertExecutor")
    public void dismissAllAlerts(String level) {
        log.info("Reconociendo todas las alertas de nivel: {}", level);
        
        List<Alert> alerts = alertRepository.findByLevelAndAcknowledgedFalse(Alert.AlertLevel.valueOf(level));
        
        alerts.forEach(alert -> {
            alert.setAcknowledged(true);
            alert.setAcknowledgedAt(LocalDateTime.now());
            alert.setAcknowledgedBy("SYSTEM");
            alertRepository.save(alert);
        });
        
        log.info("Se reconocieron {} alertas", alerts.size());
    }
    
    public long getUnacknowledgedCount() {
        log.debug("Contando alertas no reconocidas");
        return alertRepository.countUnacknowledged();
    }
    
    public long getCriticalUnacknowledgedCount() {
        log.debug("Contando alertas críticas no reconocidas");
        return alertRepository.countCriticalUnacknowledged();
    }
    
    public List<Alert> getAlertsForSensor(Long sensorId) {
        log.debug("Obteniendo alertas para sensor: {}", sensorId);
        return alertRepository.findAll().stream()
            .filter(alert -> alert.getSensor().getId().equals(sensorId))
            .toList();
    }
}

