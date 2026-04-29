package com.distribuidos.stark.sensor.movimiento.repository;

import com.distribuidos.stark.sensor.movimiento.model.MovimientoLectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovimientoLecturaRepository extends JpaRepository<MovimientoLectura, Long> {
    List<MovimientoLectura> findByEsAnomalia(boolean esAnomalia);
    Optional<MovimientoLectura> findTopByOrderByTimestampDesc();
}