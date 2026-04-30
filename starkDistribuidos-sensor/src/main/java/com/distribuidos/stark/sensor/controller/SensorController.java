package com.distribuidos.stark.sensor.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.distribuidos.stark.sensor.model.Sensor;
import com.distribuidos.stark.sensor.repository.SensorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    
    @Autowired
    private SensorRepository sensorRepository;
    private final Random random = new Random();

    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();

        // Generar valores realistas para cada sensor
        sensors.forEach(sensor -> {
            if (sensor.sensorValue == null) {
                sensor.sensorValue = generateValueForType(sensor.type);
            }
            sensor.lastUpdate = LocalDateTime.now();
        });

        return ResponseEntity.ok(sensors);
    }
    
    @PostMapping
    public ResponseEntity<Sensor> createSensor(@RequestBody Sensor sensor) {
        // Valores por defecto
        if (sensor.status == null) {
            sensor.status = "ACTIVE";
        }
        if (sensor.sensorValue == null) {
            sensor.sensorValue = generateValueForType(sensor.type);
        }

        Sensor saved = sensorRepository.save(sensor);
        return ResponseEntity.ok(saved);
    }

    /**
     * Genera un valor realista según el tipo de sensor
     */
    private Double generateValueForType(String type) {
        if (type == null) return 0.0;

        return switch (type.toUpperCase()) {
            case "TEMPERATURE" -> 15.0 + random.nextDouble() * 25; // 15-40°C
            case "HUMIDITY" -> 30.0 + random.nextDouble() * 60; // 30-90%
            case "MOTION" -> random.nextDouble() * 100; // 0-100 (intensidad)
            case "INTRUSION" -> (double) random.nextInt(2); // 0 o 1 (boolean)
            case "SMOKE" -> random.nextDouble() * 50; // 0-50 (ppm)
            case "CO2" -> 300.0 + random.nextDouble() * 700; // 300-1000 ppm
            default -> random.nextDouble() * 100;
        };
    }
}


