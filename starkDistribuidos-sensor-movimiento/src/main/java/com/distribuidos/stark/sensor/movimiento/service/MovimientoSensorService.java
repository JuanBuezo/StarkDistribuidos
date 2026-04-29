package com.distribuidos.stark.sensor.movimiento.service;

import com.distribuidos.stark.sensor.movimiento.model.MovimientoLectura;
import com.distribuidos.stark.sensor.movimiento.repository.MovimientoLecturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class MovimientoSensorService {

    private static final String[] ZONAS = {"SALA-A", "PASILLO-1", "GARAJE", "ENTRADA-PRINCIPAL", "LAB-1", "SALA-SERVIDORES"};
    private static final int UMBRAL_ANOMALIA = 80;

    @Autowired
    private MovimientoLecturaRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Scheduled(initialDelay = 5000, fixedDelay = 15000)
    public void generarLectura() {
        String zona = ZONAS[random.nextInt(ZONAS.length)];
        int intensidad = random.nextInt(101);
        boolean detectado = intensidad > 10;
        boolean esAnomalia = intensidad > UMBRAL_ANOMALIA;

        MovimientoLectura lectura = new MovimientoLectura(zona, intensidad, detectado, LocalDateTime.now(), esAnomalia);
        repository.save(lectura);

        if (esAnomalia) {
            enviarAlerta(lectura);
        }
    }

    private void enviarAlerta(MovimientoLectura lectura) {
        try {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "MOVIMIENTO_ANOMALO");
            alerta.put("fuente", "stark-sensor-movimiento");
            alerta.put("zona", lectura.getZona());
            alerta.put("intensidad", lectura.getIntensidad());
            alerta.put("timestamp", lectura.getTimestamp().toString());
            alerta.put("mensaje", "Movimiento inusual detectado en " + lectura.getZona()
                    + " con intensidad " + lectura.getIntensidad() + "%");
            restTemplate.postForObject("http://localhost:8083/api/alerts", alerta, Object.class);
        } catch (Exception ignored) {
            // El servicio de alertas puede no estar disponible
        }
    }

    public List<MovimientoLectura> obtenerTodas() {
        return repository.findAll();
    }

    public List<MovimientoLectura> obtenerAnomalias() {
        return repository.findByEsAnomalia(true);
    }

    public Optional<MovimientoLectura> obtenerUltima() {
        return repository.findTopByOrderByTimestampDesc();
    }

    public MovimientoLectura guardar(MovimientoLectura lectura) {
        lectura.setTimestamp(LocalDateTime.now());
        boolean esAnomalia = lectura.getIntensidad() > UMBRAL_ANOMALIA;
        lectura.setEsAnomalia(esAnomalia);
        lectura.setDetectado(lectura.getIntensidad() > 10);
        MovimientoLectura guardada = repository.save(lectura);
        if (esAnomalia) {
            enviarAlerta(guardada);
        }
        return guardada;
    }
}