package com.distribuidos.stark.access.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCESS_LOGS")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String username;

    @Column
    public Integer sensorId;

    @Column
    public String location;

    @Column(nullable = false)
    public Boolean granted;

    @Column
    public String ipAddress;

    @Column(nullable = false)
    public LocalDateTime timestamp;

    @Column(nullable = false)
    public Boolean hidden = false; // Para soft delete

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
