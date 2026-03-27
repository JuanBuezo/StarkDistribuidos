package com.distribuidos.stark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para servir archivos estáticos del frontend
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde la carpeta resources/static
        registry.addResourceHandler("/stark-security/static/**")
                .addResourceLocations("classpath:/static/");
        
        registry.addResourceHandler("/stark-security/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        registry.addResourceHandler("/stark-security/styles/**")
                .addResourceLocations("classpath:/static/styles/");
        
        registry.addResourceHandler("/stark-security/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        registry.addResourceHandler("/stark-security/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}

