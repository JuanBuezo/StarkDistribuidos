package com.distribuidos.stark.sensor.temperatura.service;

import com.distribuidos.stark.sensor.temperatura.model.TemperaturaLectura;
import com.distribuidos.stark.sensor.temperatura.repository.TemperaturaLecturaRepository;
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
public class TemperaturaSensorService {

    private static final String[] SALAS = {"SALA-A", "SALA-B", "LAB-1", "SALA-SERVIDORES", "GARAJE", "PASILLO-1"};
    private static final double TEMP_MIN_NORMAL = 5.0;
    private static final double TEMP_MAX_NORMAL = 35.0;

    @Autowired
    private TemperaturaLecturaRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Scheduled(initialDelay = 5000, fixedDelay = 15000)
    public void generarLectura() {
        String sala = SALAS[random.nextInt(SALAS.length)];
        // Rango general: -10°C a 50°C; la mayoría en rango normal, 15% de probabilidad de anomalía
        double temperatura = random.nextDouble() < 0.15
                ? (random.nextBoolean() ? 36.0 + random.nextDouble() * 14 : -10.0 + random.nextDouble() * 14)
                : TEMP_MIN_NORMAL + random.nextDouble() * (TEMP_MAX_NORMAL - TEMP_MIN_NORMAL);
        temperatura = Math.round(temperatura * 10.0) / 10.0;

        boolean esAnomalia = temperatura < TEMP_MIN_NORMAL || temperatura > TEMP_MAX_NORMAL;

        TemperaturaLectura lectura = new TemperaturaLectura(sala, temperatura, "CELSIUS", LocalDateTime.now(), esAnomalia);
        repository.save(lectura);

        if (esAnomalia) {
            enviarAlerta(lectura);
        }
    }

    private void enviarAlerta(TemperaturaLectura lectura) {
        try {
            String tipoAlerta = lectura.getTemperatura() > TEMP_MAX_NORMAL ? "TEMPERATURA_ALTA" : "TEMPERATURA_BAJA";
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", tipoAlerta);
            alerta.put("fuente", "stark-sensor-temperatura");
            alerta.put("sala", lectura.getSala());
            alerta.put("temperatura", lectura.getTemperatura());
            alerta.put("unidad", lectura.getUnidad());
            alerta.put("timestamp", lectura.getTimestamp().toString());
            alerta.put("mensaje", "Temperatura fuera de rango en " + lectura.getSala()
                    + ": " + lectura.getTemperatura() + "°C");
            restTemplate.postForObject("http://localhost:8083/api/alerts", alerta, Object.class);
        } catch (Exception ignored) {
            // El servicio de alertas puede no estar disponible
        }
    }

    public List<TemperaturaLectura> obtenerTodas() {
        return repository.findAll();
    }

    public List<TemperaturaLectura> obtenerAnomalias() {
        return repository.findByEsAnomalia(true);
    }

    public Optional<TemperaturaLectura> obtenerUltima() {
        return repository.findTopByOrderByTimestampDesc();
    }

    public TemperaturaLectura guardar(TemperaturaLectura lectura) {
        lectura.setTimestamp(LocalDateTime.now());
        lectura.setUnidad("CELSIUS");
        boolean esAnomalia = lectura.getTemperatura() < TEMP_MIN_NORMAL || lectura.getTemperatura() > TEMP_MAX_NORMAL;
        lectura.setEsAnomalia(esAnomalia);
        TemperaturaLectura guardada = repository.save(lectura);
        if (esAnomalia) {
            enviarAlerta(guardada);
        }
        return guardada;
    }
}