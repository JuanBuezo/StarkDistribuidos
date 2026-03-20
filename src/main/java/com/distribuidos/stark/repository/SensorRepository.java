package com.distribuidos.stark.repository;

import com.distribuidos.stark.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceso a datos de sensores.
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    
    Optional<Sensor> findByName(String name);
    
    List<Sensor> findByActive(Boolean active);
    
    List<Sensor> findByType(Sensor.SensorType type);
    
    List<Sensor> findByLocation(String location);
    
    @Query("SELECT s FROM Sensor s WHERE s.active = true ORDER BY s.name")
    List<Sensor> findAllActiveSensors();
    
    @Query("SELECT s FROM Sensor s WHERE s.type = :type AND s.active = true")
    List<Sensor> findActiveSensorsByType(@Param("type") Sensor.SensorType type);
    
    @Query("SELECT s FROM Sensor s WHERE s.failureCount > :threshold")
    List<Sensor> findFailingSensors(@Param("threshold") Integer threshold);
    
    long countByActive(Boolean active);
    
    long countByType(Sensor.SensorType type);
}

