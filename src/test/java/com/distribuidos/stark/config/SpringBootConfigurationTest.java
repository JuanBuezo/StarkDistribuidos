package com.distribuidos.stark.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de configuración para verificar que todas las configuraciones
 * de Spring se cargan correctamente.
 */
@SpringBootTest
class SpringBootConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void securityConfigBeanExists() {
        assertThat(applicationContext.containsBean("securityConfig")).isTrue();
    }

    @Test
    void asyncConfigBeanExists() {
        assertThat(applicationContext.containsBean("asyncConfig")).isTrue();
    }

    @Test
    void webSocketConfigBeanExists() {
        assertThat(applicationContext.containsBean("webSocketConfig")).isTrue();
    }

    @Test
    void taskExecutorBeanExists() {
        assertThat(applicationContext.containsBean("taskExecutor")).isTrue();
    }

    @Test
    void sensorExecutorBeanExists() {
        assertThat(applicationContext.containsBean("sensorExecutor")).isTrue();
    }

    @Test
    void alertExecutorBeanExists() {
        assertThat(applicationContext.containsBean("alertExecutor")).isTrue();
    }

    @Test
    void passwordEncoderBeanExists() {
        assertThat(applicationContext.containsBean("passwordEncoder")).isTrue();
    }

    @Test
    void userDetailsServiceBeanExists() {
        assertThat(applicationContext.containsBean("userDetailsService")).isTrue();
    }
}

