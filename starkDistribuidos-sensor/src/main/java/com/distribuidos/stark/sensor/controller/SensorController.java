package com.distribuidos.stark.sensor.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.distribuidos.stark.sensor.model.Sensor;
import com.distribuidos.stark.sensor.repository.SensorRepository;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    
    @Autowired
    private SensorRepository sensorRepository;
    
    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors() {
        return ResponseEntity.ok(sensorRepository.findAll());
    }
    
    @PostMapping
    public ResponseEntity<Sensor> createSensor(@RequestBody Sensor sensor) {
        Sensor saved = sensorRepository.save(sensor);
        return ResponseEntity.ok(saved);
    }
}

