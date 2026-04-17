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
 * Entidad que registra los intentos de acceso en el sistema.
 */
@Entity
@Table(name = "access_logs", indexes = {
    @Index(name = "idx_access_sensor", columnList = "sensor_id"),
    @Index(name = "idx_access_user", columnList = "username"),
    @Index(name = "idx_access_status", columnList = "status"),
    @Index(name = "idx_access_timestamp", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El sensor es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;
    
    @NotBlank(message = "El usuario no puede estar vacío")
    @Column(nullable = false)
    private String username;
    
    @NotNull(message = "El estado es requerido")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessStatus status; // GRANTED, DENIED, FAILED
    
    @Column
    private String ipAddress;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Version
    private Long version;
    
    public enum AccessStatus {
        GRANTED("Acceso Permitido"),
        DENIED("Acceso Denegado"),
        FAILED("Intento Fallido");
        
        private final String description;
        
        AccessStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

