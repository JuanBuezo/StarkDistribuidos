package com.distribuidos.stark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración de JPA para la persistencia de datos.
 * 
 * Habilita:
 * - Auditoría automática (campos de creación/modificación)
 * - Escaneo de repositorios Spring Data JPA
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.distribuidos.stark.repository")
@EnableJpaAuditing
public class JpaConfig {
}

