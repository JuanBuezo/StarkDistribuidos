package com.distribuidos.stark.sensor.acceso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AccesoSensorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccesoSensorApplication.class, args);
    }
}