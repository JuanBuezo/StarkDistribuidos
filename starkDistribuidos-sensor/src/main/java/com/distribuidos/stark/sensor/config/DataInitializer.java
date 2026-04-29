package com.distribuidos.stark.sensor.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.distribuidos.stark.sensor.model.Sensor;
import com.distribuidos.stark.sensor.repository.SensorRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initSensorData(SensorRepository sensorRepository) {
        return args -> {
            // Solo crear sensores si no existen
            if (sensorRepository.findAll().isEmpty()) {
                // Sensor 1: Motion Detection
                Sensor motion = new Sensor();
                motion.name = "Motion Sensor A1";
                motion.type = "MOTION";
                motion.location = "Entrada Principal";
                motion.sensorValue = 0.0;
                motion.status = "ACTIVE";
                sensorRepository.save(motion);

                // Sensor 2: Temperature
                Sensor temp = new Sensor();
                temp.name = "Temperature Sensor B2";
                temp.type = "TEMPERATURE";
                temp.location = "Sala de Servidores";
                temp.sensorValue = 22.5;
                temp.status = "ACTIVE";
                sensorRepository.save(temp);

                // Sensor 3: Humidity
                Sensor humidity = new Sensor();
                humidity.name = "Humidity Sensor C3";
                humidity.type = "HUMIDITY";
                humidity.location = "Almacén de Equipos";
                humidity.sensorValue = 45.0;
                humidity.status = "ACTIVE";
                sensorRepository.save(humidity);

                // Sensor 4: Intrusion Detection
                Sensor intrusion = new Sensor();
                intrusion.name = "Intrusion Detector D4";
                intrusion.type = "INTRUSION";
                intrusion.location = "Bóveda de Seguridad";
                intrusion.sensorValue = 0.0;
                intrusion.status = "ACTIVE";
                sensorRepository.save(intrusion);

                // Sensor 5: Smoke Detection
                Sensor smoke = new Sensor();
                smoke.name = "Smoke Detector E5";
                smoke.type = "SMOKE";
                smoke.location = "Pasillo Principal";
                smoke.sensorValue = 0.0;
                smoke.status = "ACTIVE";
                sensorRepository.save(smoke);

                // Sensor 6: CO2 Levels
                Sensor co2 = new Sensor();
                co2.name = "CO2 Monitor F6";
                co2.type = "CO2";
                co2.location = "Centro de Control";
                co2.sensorValue = 420.0;
                co2.status = "ACTIVE";
                sensorRepository.save(co2);

                System.out.println("✅ 6 sensores de prueba creados exitosamente");
            }
        };
    }
}


