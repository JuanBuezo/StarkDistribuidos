# 📝 Ejemplos de Código - Próximas Fases

Este archivo contiene ejemplos de cómo implementar las próximas fases del proyecto.

---

## 📂 FASE 2: ENTIDADES JPA

### Ejemplo 1: Entidad Sensor

```java
package com.distribuidos.stark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensors")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String type; // MOTION, TEMPERATURE, ACCESS
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column
    private Float lastValue;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### Ejemplo 2: Entidad Alert

```java
package com.distribuidos.stark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;
    
    @Column(nullable = false)
    private String level; // CRITICAL, WARNING, INFO
    
    @Column(nullable = false)
    private String message;
    
    @Column(nullable = false)
    private Boolean acknowledged = false;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

---

## 📚 FASE 3: REPOSITORIOS

### Ejemplo: SensorRepository

```java
package com.distribuidos.stark.repository;

import com.distribuidos.stark.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    
    // Búsqueda personalizada
    Optional<Sensor> findByName(String name);
    
    List<Sensor> findByActive(Boolean active);
    
    List<Sensor> findByType(String type);
    
    List<Sensor> findByLocation(String location);
    
    // Consulta personalizada
    @Query("SELECT s FROM Sensor s WHERE s.active = true ORDER BY s.name")
    List<Sensor> findAllActiveSensors();
}
```

---

## 🔧 FASE 4: SERVICIOS

### Ejemplo: SensorService

```java
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

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    
    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public List<Sensor> getAllSensors() {
        log.info("Obteniendo todos los sensores");
        return sensorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Sensor> getSensorById(Long id) {
        log.info("Obteniendo sensor: {}", id);
        return sensorRepository.findById(id);
    }
    
    @Transactional
    public Sensor createSensor(Sensor sensor) {
        log.info("Creando sensor: {}", sensor.getName());
        return sensorRepository.save(sensor);
    }
    
    @Transactional
    @Async("sensorExecutor")
    public void processSensorData(Long sensorId, Float value) {
        log.debug("Procesando datos del sensor {} con valor {}", sensorId, value);
        
        Optional<Sensor> sensor = sensorRepository.findById(sensorId);
        if (sensor.isPresent()) {
            Sensor s = sensor.get();
            s.setLastValue(value);
            sensorRepository.save(s);
            
            // Notificar cambio
            notificationService.notifySensorUpdate(s);
        }
    }
    
    @Async("sensorExecutor")
    public void processBatchSensorData(List<Sensor> sensors) {
        log.info("Procesando batch de {} sensores", sensors.size());
        
        sensors.parallelStream()
            .forEach(sensor -> processSensorData(sensor.getId(), sensor.getLastValue()));
    }
}
```

### Ejemplo: AlertService

```java
package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    
    private final AlertRepository alertRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    @Transactional
    @Async("alertExecutor")
    public void createAlert(Sensor sensor, String level, String message) {
        log.warn("Creando alerta nivel {} para sensor: {}", level, sensor.getName());
        
        Alert alert = new Alert();
        alert.setSensor(sensor);
        alert.setLevel(level);
        alert.setMessage(message);
        alert.setAcknowledged(false);
        
        Alert savedAlert = alertRepository.save(alert);
        
        // Enviar notificaciones
        notifyAlert(savedAlert);
    }
    
    @Async("alertExecutor")
    private void notifyAlert(Alert alert) {
        log.info("Notificando alerta: {}", alert.getId());
        
        // Enviar por WebSocket
        notificationService.publishAlert(alert);
        
        // Enviar por email si es crítico
        if ("CRITICAL".equals(alert.getLevel())) {
            emailService.sendCriticalAlert(alert);
        }
    }
    
    @Transactional
    public void acknowledgeAlert(Long alertId) {
        log.info("Reconociendo alerta: {}", alertId);
        
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.setAcknowledged(true);
            alertRepository.save(alert);
        });
    }
    
    @Transactional(readOnly = true)
    public List<Alert> getUnacknowledgedAlerts() {
        log.info("Obteniendo alertas no reconocidas");
        return alertRepository.findByAcknowledgedFalse();
    }
}
```

---

## 🌐 FASE 5: CONTROLADORES REST

### Ejemplo: SensorController

```java
package com.distribuidos.stark.controller;

import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stark-security/sensors")
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
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        log.info("GET /sensors/{} - Obteniendo sensor", id);
        return sensorService.getSensorById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sensor> createSensor(@RequestBody Sensor sensor) {
        log.info("POST /sensors - Creando sensor: {}", sensor.getName());
        Sensor created = sensorService.createSensor(sensor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}/data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Void> updateSensorData(
            @PathVariable Long id,
            @RequestParam Float value) {
        log.info("PUT /sensors/{}/data - Actualizando con valor {}", id, value);
        sensorService.processSensorData(id, value);
        return ResponseEntity.noContent().build();
    }
}
```

### Ejemplo: AlertController

```java
package com.distribuidos.stark.controller;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stark-security/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {
    
    private final AlertService alertService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<List<Alert>> getUnacknowledgedAlerts() {
        log.info("GET /alerts - Obteniendo alertas no reconocidas");
        List<Alert> alerts = alertService.getUnacknowledgedAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECURITY')")
    public ResponseEntity<Void> acknowledgeAlert(@PathVariable Long id) {
        log.info("PUT /alerts/{}/acknowledge - Reconociendo alerta", id);
        alertService.acknowledgeAlert(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 📡 FASE 6: WEBSOCKET HANDLERS

### Ejemplo: NotificationHandler

```java
package com.distribuidos.stark.websocket;

import com.distribuidos.stark.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendAlert(Alert alert) {
        log.info("Enviando alerta por WebSocket: {}", alert.getId());
        
        messagingTemplate.convertAndSend(
            "/topic/alerts",
            alert
        );
    }
    
    public void sendToUser(String username, String message) {
        log.info("Enviando mensaje a usuario: {}", username);
        
        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/messages",
            message
        );
    }
    
    public void broadcastSystemStatus(String status) {
        log.info("Broadcasting estado del sistema: {}", status);
        
        messagingTemplate.convertAndSend(
            "/topic/system-status",
            status
        );
    }
}
```

---

## 🧪 EJEMPLOS DE TESTING

### Ejemplo: SensorServiceTest

```java
package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Sensor;
import com.distribuidos.stark.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SensorServiceTest {
    
    @Autowired
    private SensorService sensorService;
    
    @Autowired
    private SensorRepository sensorRepository;
    
    @BeforeEach
    void setUp() {
        sensorRepository.deleteAll();
    }
    
    @Test
    void testCreateSensor() {
        // Arrange
        Sensor sensor = new Sensor();
        sensor.setName("Motion Sensor 1");
        sensor.setType("MOTION");
        sensor.setLocation("Entrada Principal");
        
        // Act
        Sensor created = sensorService.createSensor(sensor);
        
        // Assert
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Motion Sensor 1");
    }
    
    @Test
    void testGetAllSensors() {
        // Arrange
        Sensor sensor1 = new Sensor();
        sensor1.setName("Sensor 1");
        sensor1.setType("MOTION");
        sensor1.setLocation("Zona A");
        
        sensorService.createSensor(sensor1);
        
        // Act
        var sensors = sensorService.getAllSensors();
        
        // Assert
        assertThat(sensors).isNotEmpty();
        assertThat(sensors.size()).isGreaterThanOrEqualTo(1);
    }
}
```

---

## 💾 ESTRUCTURA DE CARPETAS RECOMENDADA

```
src/main/java/com/distribuidos/stark/
├── config/                          ✅ (LISTO)
│   ├── SecurityConfig.java
│   ├── AsyncConfig.java
│   ├── WebSocketConfig.java
│   └── JpaConfig.java
│
├── entity/                          ⏳ (PRÓXIMO)
│   ├── Sensor.java
│   ├── Alert.java
│   ├── AccessLog.java
│   └── SecurityUser.java
│
├── dto/                             ⏳ (DATA TRANSFER OBJECTS)
│   ├── SensorDTO.java
│   └── AlertDTO.java
│
├── repository/                      ⏳ (DATA ACCESS)
│   ├── SensorRepository.java
│   ├── AlertRepository.java
│   └── AccessLogRepository.java
│
├── service/                         ⏳ (BUSINESS LOGIC)
│   ├── SensorService.java
│   ├── AlertService.java
│   ├── AccessControlService.java
│   ├── NotificationService.java
│   └── EmailService.java
│
├── controller/                      ⏳ (REST API)
│   ├── SensorController.java
│   ├── AlertController.java
│   └── AccessController.java
│
├── websocket/                       ⏳ (REAL-TIME COMMUNICATION)
│   ├── NotificationHandler.java
│   ├── SensorDataHandler.java
│   └── AlertHandler.java
│
├── exception/                       ⏳ (ERROR HANDLING)
│   ├── SensorNotFoundException.java
│   └── GlobalExceptionHandler.java
│
└── StarkDistribuidosApplication.java ✅ (ACTUALIZADO)
```

---

## 🚀 ORDEN RECOMENDADO DE IMPLEMENTACIÓN

1. ✅ Configuración base (COMPLETADO)
2. ⏳ Crear entidades JPA
3. ⏳ Crear repositorios
4. ⏳ Crear servicios
5. ⏳ Crear controladores REST
6. ⏳ Crear WebSocket handlers
7. ⏳ Implementar validaciones
8. ⏳ Escribir tests
9. ⏳ Optimizar rendimiento
10. ⏳ Documentar API (Swagger)

---

**Este archivo proporciona ejemplos listos para copiar y adaptar en la siguiente fase.**

