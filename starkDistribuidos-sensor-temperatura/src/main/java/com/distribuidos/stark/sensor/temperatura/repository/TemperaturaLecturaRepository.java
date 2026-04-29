package com.distribuidos.stark.sensor.temperatura.repository;

import com.distribuidos.stark.sensor.temperatura.model.TemperaturaLectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemperaturaLecturaRepository extends JpaRepository<TemperaturaLectura, Long> {
    List<TemperaturaLectura> findByEsAnomalia(boolean esAnomalia);
    Optional<TemperaturaLectura> findTopByOrderByTimestampDesc();
}