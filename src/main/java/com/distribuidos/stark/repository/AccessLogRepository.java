package com.distribuidos.stark.repository;

import com.distribuidos.stark.entity.AccessLog;
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
 * Repositorio para acceso a datos de logs de acceso.
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
    List<AccessLog> findByUsername(String username);
    
    List<AccessLog> findByStatus(AccessLog.AccessStatus status);
    
    List<AccessLog> findBySensor(Sensor sensor);
    
    @Query("SELECT a FROM AccessLog a WHERE a.status = 'DENIED' ORDER BY a.createdAt DESC")
    List<AccessLog> findDeniedAttempts();
    
    @Query("SELECT a FROM AccessLog a WHERE a.status = 'FAILED' ORDER BY a.createdAt DESC")
    List<AccessLog> findFailedAttempts();
    
    @Query("SELECT a FROM AccessLog a WHERE a.username = :username ORDER BY a.createdAt DESC")
    Page<AccessLog> findAccessLogsByUsername(@Param("username") String username, Pageable pageable);
    
    @Query("SELECT a FROM AccessLog a WHERE a.sensor = :sensor ORDER BY a.createdAt DESC")
    Page<AccessLog> findAccessLogsBySensor(@Param("sensor") Sensor sensor, Pageable pageable);
    
    @Query("SELECT a FROM AccessLog a WHERE a.createdAt >= :startDate ORDER BY a.createdAt DESC")
    List<AccessLog> findAccessLogsAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(a) FROM AccessLog a WHERE a.username = :username AND a.status = 'DENIED'")
    long countDeniedAccessForUser(@Param("username") String username);
    
    @Query("SELECT a FROM AccessLog a WHERE a.username = :username AND a.status = 'DENIED' AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AccessLog> findRecentDeniedAccessForUser(@Param("username") String username, @Param("since") LocalDateTime since);
}

