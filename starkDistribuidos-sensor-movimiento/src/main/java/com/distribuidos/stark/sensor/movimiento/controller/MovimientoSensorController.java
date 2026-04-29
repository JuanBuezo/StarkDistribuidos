package com.distribuidos.stark.sensor.movimiento.controller;

import com.distribuidos.stark.sensor.movimiento.model.MovimientoLectura;
import com.distribuidos.stark.sensor.movimiento.service.MovimientoSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motion")
public class MovimientoSensorController {

    @Autowired
    private MovimientoSensorService service;

    @GetMapping
    public ResponseEntity<List<MovimientoLectura>> getAll() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/anomalias")
    public ResponseEntity<List<MovimientoLectura>> getAnomalias() {
        return ResponseEntity.ok(service.obtenerAnomalias());
    }

    @GetMapping("/ultimo")
    public ResponseEntity<?> getUltimo() {
        return service.obtenerUltima()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovimientoLectura> create(@RequestBody MovimientoLectura lectura) {
        return ResponseEntity.ok(service.guardar(lectura));
    }
}