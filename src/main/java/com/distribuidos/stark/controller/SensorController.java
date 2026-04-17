package com.distribuidos.stark.controller;

import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de sensores.
 */
@RestController
@RequestMapping("/stark-security/api/sensors")
@RequiredArgsConstructor
@Slf4j
public class SensorController {
    
    private final SensorService sensorService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Sensor>> getAllSensors() {
        log.info("GET /sensors - Obteniendo todos los sensores");
        List<Sensor> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Sensor>> getActiveSensors() {
        log.info("GET /sensors/active - Obteniendo sensores activos");
        List<Sensor> sensors = sensorService.getActiveSensors();
        return ResponseEntity.ok(sensors);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        log.info("GET /sensors/{} - Obteniendo sensor", id);
        return sensorService.getSensorById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Sensor> getSensorByName(@PathVariable String name) {
        log.info("GET /sensors/name/{} - Obteniendo sensor por nombre", name);
        return sensorService.getSensorByName(name)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Sensor>> getSensorsByType(@PathVariable String type) {
        log.info("GET /sensors/type/{} - Obteniendo sensores por tipo", type);
        try {
            Sensor.SensorType sensorType = Sensor.SensorType.valueOf(type.toUpperCase());
            List<Sensor> sensors = sensorService.getSensorsByType(sensorType);
            return ResponseEntity.ok(sensors);
        } catch (IllegalArgumentException e) {
            log.error("Tipo de sensor inválido: {}", type);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/location/{location}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Sensor>> getSensorsByLocation(@PathVariable String location) {
        log.info("GET /sensors/location/{} - Obteniendo sensores por ubicación", location);
        List<Sensor> sensors = sensorService.getSensorsByLocation(location);
        return ResponseEntity.ok(sensors);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sensor> createSensor(@Valid @RequestBody Sensor sensor) {
        log.info("POST /sensors - Creando nuevo sensor: {}", sensor.getName());
        try {
            Sensor created = sensorService.createSensor(sensor);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear sensor: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sensor> updateSensor(
            @PathVariable Long id,
            @Valid @RequestBody Sensor sensorDetails) {
        log.info("PUT /sensors/{} - Actualizando sensor", id);
        try {
            Sensor updated = sensorService.updateSensor(id, sensorDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error al actualizar sensor: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        log.info("DELETE /sensors/{} - Eliminando sensor", id);
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateSensor(@PathVariable Long id) {
        log.info("PUT /sensors/{}/activate - Activando sensor", id);
        sensorService.activateSensor(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateSensor(@PathVariable Long id) {
        log.info("PUT /sensors/{}/deactivate - Desactivando sensor", id);
        sensorService.deactivateSensor(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Void> updateSensorData(
            @PathVariable Long id,
            @RequestParam Float value,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        log.info("PUT /sensors/{}/data - Actualizando datos: valor={}, estado={}", id, value, status);
        sensorService.processSensorData(id, value, status);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/reset-failures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetFailures(@PathVariable Long id) {
        log.info("PUT /sensors/{}/reset-failures - Reseteando fallos", id);
        sensorService.resetSensorFailures(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/failing")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Sensor>> getFailingSensors(
            @RequestParam(defaultValue = "3") Integer threshold) {
        log.info("GET /sensors/failing - Obteniendo sensores con {} o más fallos", threshold);
        List<Sensor> sensors = sensorService.getFailingSensors(threshold);
        return ResponseEntity.ok(sensors);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<SensorStats> getSensorStats() {
        log.info("GET /sensors/stats - Obteniendo estadísticas de sensores");
        SensorStats stats = SensorStats.builder()
            .totalActive(sensorService.getActiveSensorCount())
            .totalMotion(sensorService.getSensorCountByType(Sensor.SensorType.MOTION))
            .totalTemperature(sensorService.getSensorCountByType(Sensor.SensorType.TEMPERATURE))
            .totalAccess(sensorService.getSensorCountByType(Sensor.SensorType.ACCESS))
            .build();
        return ResponseEntity.ok(stats);
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SensorStats {
        private long totalActive;
        private long totalMotion;
        private long totalTemperature;
        private long totalAccess;
    }
}

