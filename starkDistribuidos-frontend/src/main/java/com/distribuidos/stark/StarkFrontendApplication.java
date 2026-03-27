package com.distribuidos.stark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Microservicio Frontend - Stark Industries
 * 
 * Proporciona:
 * - Interfaz web de usuario (HTML/CSS/JavaScript)
 * - Sistema de autenticación
 * - Dashboard de telemetría
 * - Comunicación en tiempo real vía WebSocket
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
public class StarkFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarkFrontendApplication.class, args);
    }

}


