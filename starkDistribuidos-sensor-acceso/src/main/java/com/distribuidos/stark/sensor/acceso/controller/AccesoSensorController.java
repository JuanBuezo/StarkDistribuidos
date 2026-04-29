package com.distribuidos.stark.sensor.acceso.controller;

import com.distribuidos.stark.sensor.acceso.model.AccesoLectura;
import com.distribuidos.stark.sensor.acceso.service.AccesoSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-events")
public class AccesoSensorController {

    @Autowired
    private AccesoSensorService service;

    @GetMapping
    public ResponseEntity<List<AccesoLectura>> getAll() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/anomalias")
    public ResponseEntity<List<AccesoLectura>> getAnomalias() {
        return ResponseEntity.ok(service.obtenerAnomalias());
    }

    @GetMapping("/no-autorizados")
    public ResponseEntity<List<AccesoLectura>> getNoAutorizados() {
        return ResponseEntity.ok(service.obtenerNoAutorizados());
    }

    @GetMapping("/ultimo")
    public ResponseEntity<?> getUltimo() {
        return service.obtenerUltima()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccesoLectura> create(@RequestBody AccesoLectura lectura) {
        return ResponseEntity.ok(service.guardar(lectura));
    }
}