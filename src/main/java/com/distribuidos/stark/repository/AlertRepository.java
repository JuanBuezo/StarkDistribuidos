package com.distribuidos.stark.repository;

import com.distribuidos.stark.entity.Alert;
import com.distribuidos.stark.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para acceso a datos de alertas.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByAcknowledgedFalse();
    
    List<Alert> findByAcknowledgedTrue();
    
    List<Alert> findBySensor(Sensor sensor);
    
    List<Alert> findByLevel(Alert.AlertLevel level);
    
    List<Alert> findByLevelAndAcknowledgedFalse(Alert.AlertLevel level);
    
    @Query("SELECT a FROM Alert a WHERE a.acknowledged = false ORDER BY a.createdAt DESC")
    List<Alert> findUnacknowledgedAlerts();
    
    @Query("SELECT a FROM Alert a WHERE a.level = 'CRITICAL' AND a.acknowledged = false ORDER BY a.createdAt DESC")
    List<Alert> findCriticalUnacknowledgedAlerts();
    
    @Query("SELECT a FROM Alert a WHERE a.createdAt >= :startDate ORDER BY a.createdAt DESC")
    List<Alert> findAlertsAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a FROM Alert a WHERE a.sensor = :sensor ORDER BY a.createdAt DESC")
    Page<Alert> findAlertsBySensor(@Param("sensor") Sensor sensor, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.acknowledged = false")
    long countUnacknowledged();
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.level = 'CRITICAL' AND a.acknowledged = false")
    long countCriticalUnacknowledged();
}

