package com.distribuidos.stark.sensor.acceso.service;

import com.distribuidos.stark.sensor.acceso.model.AccesoLectura;
import com.distribuidos.stark.sensor.acceso.repository.AccesoLecturaRepository;
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
public class AccesoSensorService {

    private static final String[] ZONAS = {"ENTRADA-PRINCIPAL", "LAB-1", "SALA-SERVIDORES", "GARAJE", "SALA-A"};
    private static final String[] USUARIOS = {"USR-001", "USR-002", "USR-003", "USR-004", "DESCONOCIDO"};
    private static final String[] METODOS = {"TARJETA", "BIOMETRICO", "PIN"};

    @Autowired
    private AccesoLecturaRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Scheduled(initialDelay = 5000, fixedDelay = 20000)
    public void generarLectura() {
        String zona = ZONAS[random.nextInt(ZONAS.length)];
        String usuarioId = USUARIOS[random.nextInt(USUARIOS.length)];
        String metodo = METODOS[random.nextInt(METODOS.length)];
        // DESCONOCIDO siempre es no autorizado; 10% de probabilidad adicional de rechazo
        boolean autorizado = !usuarioId.equals("DESCONOCIDO") && random.nextDouble() > 0.10;
        boolean esAnomalia = !autorizado;

        AccesoLectura lectura = new AccesoLectura(zona, usuarioId, autorizado, metodo, LocalDateTime.now(), esAnomalia);
        repository.save(lectura);

        if (esAnomalia) {
            enviarAlerta(lectura);
        }
    }

    private void enviarAlerta(AccesoLectura lectura) {
        try {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "ACCESO_NO_AUTORIZADO");
            alerta.put("fuente", "stark-sensor-acceso");
            alerta.put("zona", lectura.getZona());
            alerta.put("usuarioId", lectura.getUsuarioId());
            alerta.put("metodo", lectura.getMetodo());
            alerta.put("timestamp", lectura.getTimestamp().toString());
            alerta.put("mensaje", "Intento de acceso no autorizado en " + lectura.getZona()
                    + " por usuario " + lectura.getUsuarioId() + " via " + lectura.getMetodo());
            restTemplate.postForObject("http://localhost:8083/api/alerts", alerta, Object.class);
        } catch (Exception ignored) {
            // El servicio de alertas puede no estar disponible
        }
    }

    public List<AccesoLectura> obtenerTodas() {
        return repository.findAll();
    }

    public List<AccesoLectura> obtenerAnomalias() {
        return repository.findByEsAnomalia(true);
    }

    public List<AccesoLectura> obtenerNoAutorizados() {
        return repository.findByAutorizado(false);
    }

    public Optional<AccesoLectura> obtenerUltima() {
        return repository.findTopByOrderByTimestampDesc();
    }

    public AccesoLectura guardar(AccesoLectura lectura) {
        lectura.setTimestamp(LocalDateTime.now());
        lectura.setEsAnomalia(!lectura.isAutorizado());
        AccesoLectura guardada = repository.save(lectura);
        if (!lectura.isAutorizado()) {
            enviarAlerta(guardada);
        }
        return guardada;
    }
}