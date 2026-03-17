package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de ejecución asincrónica para el procesamiento concurrente
 * de sensores y datos en tiempo real.
 * 
 * Define:
 * - Pool de hilos para tareas asincrónicas
 * - Parámetros de concurrencia
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configura el executor principal para tareas asincrónicas.
     * Utilizado para procesar datos de sensores de forma concurrente.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("StarkAsync-");
        executor.initialize();
        return executor;
    }

    /**
     * Executor específico para procesar sensores.
     * Permite paralelizar el procesamiento de múltiples sensores.
     */
    @Bean(name = "sensorExecutor")
    public Executor sensorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("SensorProcessor-");
        executor.initialize();
        return executor;
    }

    /**
     * Executor para notificaciones y alertas.
     * Procesa alertas de forma independiente sin bloquear el flujo principal.
     */
    @Bean(name = "alertExecutor")
    public Executor alertExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AlertNotifier-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }
}

