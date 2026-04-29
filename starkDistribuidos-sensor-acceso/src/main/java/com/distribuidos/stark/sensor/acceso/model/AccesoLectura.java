package com.distribuidos.stark.sensor.acceso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "acceso_lecturas")
public class AccesoLectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String zona;

    @Column(nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private boolean autorizado;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean esAnomalia;

    public AccesoLectura() {}

    public AccesoLectura(String zona, String usuarioId, boolean autorizado, String metodo, LocalDateTime timestamp, boolean esAnomalia) {
        this.zona = zona;
        this.usuarioId = usuarioId;
        this.autorizado = autorizado;
        this.metodo = metodo;
        this.timestamp = timestamp;
        this.esAnomalia = esAnomalia;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public boolean isAutorizado() { return autorizado; }
    public void setAutorizado(boolean autorizado) { this.autorizado = autorizado; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isEsAnomalia() { return esAnomalia; }
    public void setEsAnomalia(boolean esAnomalia) { this.esAnomalia = esAnomalia; }
}