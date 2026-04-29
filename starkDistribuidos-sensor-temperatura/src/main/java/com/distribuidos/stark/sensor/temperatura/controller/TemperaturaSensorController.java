package com.distribuidos.stark.sensor.temperatura.controller;

import com.distribuidos.stark.sensor.temperatura.model.TemperaturaLectura;
import com.distribuidos.stark.sensor.temperatura.service.TemperaturaSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temperature")
public class TemperaturaSensorController {

    @Autowired
    private TemperaturaSensorService service;

    @GetMapping
    public ResponseEntity<List<TemperaturaLectura>> getAll() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/anomalias")
    public ResponseEntity<List<TemperaturaLectura>> getAnomalias() {
        return ResponseEntity.ok(service.obtenerAnomalias());
    }

    @GetMapping("/ultimo")
    public ResponseEntity<?> getUltimo() {
        return service.obtenerUltima()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TemperaturaLectura> create(@RequestBody TemperaturaLectura lectura) {
        return ResponseEntity.ok(service.guardar(lectura));
    }
}