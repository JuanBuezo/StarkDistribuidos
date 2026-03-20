package com.distribuidos.stark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa un sensor del sistema de seguridad.
 * Puede ser de tipo movimiento, temperatura o acceso.
 */
@Entity
@Table(name = "sensors", indexes = {
    @Index(name = "idx_sensor_name", columnList = "name", unique = true),
    @Index(name = "idx_sensor_active", columnList = "active"),
    @Index(name = "idx_sensor_type", columnList = "type"),
    @Index(name = "idx_sensor_location", columnList = "location")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del sensor no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String name;
    
    @NotNull(message = "El tipo de sensor es requerido")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorType type; // MOTION, TEMPERATURE, ACCESS
    
    @NotBlank(message = "La ubicación del sensor no puede estar vacía")
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column
    private Float lastValue;
    
    @Column
    private String lastStatus;
    
    @Column
    private Integer failureCount = 0;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    public enum SensorType {
        MOTION("Sensor de Movimiento"),
        TEMPERATURE("Sensor de Temperatura"),
        ACCESS("Sensor de Acceso");
        
        private final String description;
        
        SensorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

