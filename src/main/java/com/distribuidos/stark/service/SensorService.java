package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de sensores.
 * Maneja la creación, actualización y procesamiento de datos de sensores.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SensorService {
    
    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;
    private final AlertService alertService;
    
    public List<Sensor> getAllSensors() {
        log.info("Obteniendo todos los sensores");
        return sensorRepository.findAll();
    }
    
    public List<Sensor> getActiveSensors() {
        log.info("Obteniendo sensores activos");
        return sensorRepository.findAllActiveSensors();
    }
    
    public Optional<Sensor> getSensorById(Long id) {
        log.info("Obteniendo sensor: {}", id);
        return sensorRepository.findById(id);
    }
    
    public Optional<Sensor> getSensorByName(String name) {
        log.info("Obteniendo sensor por nombre: {}", name);
        return sensorRepository.findByName(name);
    }
    
    public List<Sensor> getSensorsByType(Sensor.SensorType type) {
        log.info("Obteniendo sensores de tipo: {}", type);
        return sensorRepository.findActiveSensorsByType(type);
    }
    
    public List<Sensor> getSensorsByLocation(String location) {
        log.info("Obteniendo sensores en ubicación: {}", location);
        return sensorRepository.findByLocation(location);
    }
    
    @Transactional
    public Sensor createSensor(Sensor sensor) {
        log.info("Creando nuevo sensor: {}", sensor.getName());
        
        if (sensorRepository.findByName(sensor.getName()).isPresent()) {
            log.warn("Ya existe un sensor con el nombre: {}", sensor.getName());
            throw new IllegalArgumentException("Ya existe un sensor con este nombre");
        }
        
        Sensor created = sensorRepository.save(sensor);
        log.info("Sensor creado exitosamente con ID: {}", created.getId());
        return created;
    }
    
    @Transactional
    public Sensor updateSensor(Long id, Sensor sensorDetails) {
        log.info("Actualizando sensor: {}", id);
        
        return sensorRepository.findById(id)
            .map(sensor -> {
                sensor.setName(sensorDetails.getName());
                sensor.setType(sensorDetails.getType());
                sensor.setLocation(sensorDetails.getLocation());
                sensor.setActive(sensorDetails.getActive());
                
                Sensor updated = sensorRepository.save(sensor);
                log.info("Sensor actualizado: {}", id);
                return updated;
            })
            .orElseThrow(() -> new RuntimeException("Sensor no encontrado: " + id));
    }
    
    @Transactional
    public void deleteSensor(Long id) {
        log.info("Eliminando sensor: {}", id);
        sensorRepository.deleteById(id);
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void processSensorData(Long sensorId, Float value, String status) {
        log.debug("Procesando datos del sensor {} con valor {}", sensorId, value);
        
        sensorRepository.findById(sensorId).ifPresent(sensor -> {
            sensor.setLastValue(value);
            sensor.setLastStatus(status);
            
            // Reset failure count si el sensor está activo
            if (status.equals("ACTIVE") || status.equals("OK")) {
                sensor.setFailureCount(0);
            } else {
                sensor.setFailureCount(sensor.getFailureCount() + 1);
            }
            
            sensorRepository.save(sensor);
            
            // Notificar actualización
            notificationService.publishSensorUpdate(sensor);
            
            // Evaluar alertas si es necesario
            evaluateSensorAlerts(sensor);
        });
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void processBatchSensorData(List<Sensor> sensors) {
        log.info("Procesando batch de {} sensores", sensors.size());
        
        sensors.parallelStream().forEach(sensor -> {
            if (sensor.getLastValue() != null) {
                processSensorData(sensor.getId(), sensor.getLastValue(), sensor.getLastStatus());
            }
        });
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void activateSensor(Long id) {
        log.info("Activando sensor: {}", id);
        
        sensorRepository.findById(id).ifPresent(sensor -> {
            sensor.setActive(true);
            sensor.setFailureCount(0);
            sensorRepository.save(sensor);
            log.info("Sensor activado: {}", id);
        });
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void deactivateSensor(Long id) {
        log.info("Desactivando sensor: {}", id);
        
        sensorRepository.findById(id).ifPresent(sensor -> {
            sensor.setActive(false);
            sensorRepository.save(sensor);
            log.info("Sensor desactivado: {}", id);
        });
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void resetSensorFailures(Long id) {
        log.info("Reseteando fallos del sensor: {}", id);
        
        sensorRepository.findById(id).ifPresent(sensor -> {
            sensor.setFailureCount(0);
            sensorRepository.save(sensor);
        });
    }
    
    public List<Sensor> getFailingSensors(Integer threshold) {
        log.info("Obteniendo sensores con más de {} fallos", threshold);
        return sensorRepository.findFailingSensors(threshold);
    }
    
    private void evaluateSensorAlerts(Sensor sensor) {
        // Evaluar si se debe generar una alerta basada en los datos del sensor
        if (sensor.getFailureCount() > 3) {
            log.warn("Sensor {} tiene múltiples fallos", sensor.getId());
            alertService.createAlert(
                sensor,
                "CRITICAL",
                "El sensor " + sensor.getName() + " tiene múltiples fallos"
            );
        } else if (sensor.getFailureCount() > 1) {
            log.warn("Sensor {} está presentando problemas", sensor.getId());
            alertService.createAlert(
                sensor,
                "WARNING",
                "El sensor " + sensor.getName() + " está presentando problemas"
            );
        }
    }
    
    public long getActiveSensorCount() {
        log.debug("Contando sensores activos");
        return sensorRepository.countByActive(true);
    }
    
    public long getSensorCountByType(Sensor.SensorType type) {
        log.debug("Contando sensores de tipo: {}", type);
        return sensorRepository.countByType(type);
    }
}

