package com.distribuidos.stark.alert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ALERTS")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String level; // CRITICAL, WARNING, INFO

    @Column(nullable = false)
    public String message;

    @Column
    public String recipient;

    @Column
    public String email;

    @Column(nullable = false)
    public LocalDateTime timestamp;

    @Column
    public String status; // ACTIVE, ACKNOWLEDGED

    @Column(nullable = false)
    public Boolean hidden = false; // Para soft delete

    // Nuevos campos para relacionar con sensores
    @Column
    public Long sensorId;

    @Column
    public String sensorName;

    @Column
    public String sensorType;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = "ACTIVE";
        }
    }
}
