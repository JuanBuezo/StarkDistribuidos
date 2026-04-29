package com.distribuidos.stark.sensor.temperatura.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temperatura_lecturas")
public class TemperaturaLectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sala;

    @Column(nullable = false)
    private double temperatura;

    @Column(nullable = false)
    private String unidad;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean esAnomalia;

    public TemperaturaLectura() {}

    public TemperaturaLectura(String sala, double temperatura, String unidad, LocalDateTime timestamp, boolean esAnomalia) {
        this.sala = sala;
        this.temperatura = temperatura;
        this.unidad = unidad;
        this.timestamp = timestamp;
        this.esAnomalia = esAnomalia;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }
    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isEsAnomalia() { return esAnomalia; }
    public void setEsAnomalia(boolean esAnomalia) { this.esAnomalia = esAnomalia; }
}