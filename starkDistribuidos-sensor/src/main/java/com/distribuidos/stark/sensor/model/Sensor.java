package com.distribuidos.stark.sensor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SENSOR")
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String type; // MOTION, TEMPERATURE, HUMIDITY, INTRUSION, SMOKE, CO2

    @Column
    public String location;

    @Column(name = "SENSOR_VALUE")
    public Double sensorValue;

    @Column
    public String status; // ACTIVE, INACTIVE

    @Column(name = "LAST_UPDATE")
    public LocalDateTime lastUpdate;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdate = LocalDateTime.now();
    }
}

