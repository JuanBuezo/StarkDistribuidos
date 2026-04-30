package com.distribuidos.stark.alert.repository;

import com.distribuidos.stark.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Solo alertas visibles (no ocultas)
    List<Alert> findByHiddenFalse();

    // Todas las alertas para historial
    List<Alert> findAll();

    // Alertas por nivel (solo visibles)
    List<Alert> findByLevelAndHiddenFalse(String level);

    // Alertas activas (no reconocidas)
    List<Alert> findByStatusAndHiddenFalse(String status);
}
