package com.distribuidos.stark.sensor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.distribuidos.stark.sensor.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
