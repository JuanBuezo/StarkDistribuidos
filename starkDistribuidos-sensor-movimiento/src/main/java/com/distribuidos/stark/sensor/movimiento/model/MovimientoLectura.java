package com.distribuidos.stark.sensor.movimiento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_lecturas")
public class MovimientoLectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String zona;

    @Column(nullable = false)
    private int intensidad;

    @Column(nullable = false)
    private boolean detectado;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean esAnomalia;

    public MovimientoLectura() {}

    public MovimientoLectura(String zona, int intensidad, boolean detectado, LocalDateTime timestamp, boolean esAnomalia) {
        this.zona = zona;
        this.intensidad = intensidad;
        this.detectado = detectado;
        this.timestamp = timestamp;
        this.esAnomalia = esAnomalia;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public int getIntensidad() { return intensidad; }
    public void setIntensidad(int intensidad) { this.intensidad = intensidad; }
    public boolean isDetectado() { return detectado; }
    public void setDetectado(boolean detectado) { this.detectado = detectado; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isEsAnomalia() { return esAnomalia; }
    public void setEsAnomalia(boolean esAnomalia) { this.esAnomalia = esAnomalia; }
}