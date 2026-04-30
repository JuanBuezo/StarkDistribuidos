package com.distribuidos.stark.access.repository;

import com.distribuidos.stark.access.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    // Solo logs visibles (no ocultos)
    List<AccessLog> findByHiddenFalse();

    // Todos los logs para historial
    List<AccessLog> findAll();

    // Logs por usuario (solo visibles)
    List<AccessLog> findByUsernameAndHiddenFalse(String username);

    // Logs permitidos/denegados (solo visibles)
    List<AccessLog> findByGrantedAndHiddenFalse(Boolean granted);
}
