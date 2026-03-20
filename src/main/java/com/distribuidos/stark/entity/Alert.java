package com.distribuidos.stark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta del sistema de seguridad.
 * Se genera cuando un sensor detecta una anomalía.
 */
@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alert_sensor", columnList = "sensor_id"),
    @Index(name = "idx_alert_level", columnList = "level"),
    @Index(name = "idx_alert_acknowledged", columnList = "acknowledged"),
    @Index(name = "idx_alert_created", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El sensor es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;
    
    @NotNull(message = "El nivel de alerta es requerido")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertLevel level; // CRITICAL, WARNING, INFO
    
    @NotBlank(message = "El mensaje de alerta no puede estar vacío")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean acknowledged = false;
    
    @Column
    private LocalDateTime acknowledgedAt;
    
    @Column
    private String acknowledgedBy;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Version
    private Long version;
    
    public enum AlertLevel {
        CRITICAL("Alerta Crítica", 1),
        WARNING("Advertencia", 2),
        INFO("Información", 3);
        
        private final String description;
        private final Integer priority;
        
        AlertLevel(String description, Integer priority) {
            this.description = description;
            this.priority = priority;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Integer getPriority() {
            return priority;
        }
    }
}

