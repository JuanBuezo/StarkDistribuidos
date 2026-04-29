package com.distribuidos.stark.sensor.acceso.repository;

import com.distribuidos.stark.sensor.acceso.model.AccesoLectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccesoLecturaRepository extends JpaRepository<AccesoLectura, Long> {
    List<AccesoLectura> findByEsAnomalia(boolean esAnomalia);
    Optional<AccesoLectura> findTopByOrderByTimestampDesc();
    List<AccesoLectura> findByAutorizado(boolean autorizado);
}