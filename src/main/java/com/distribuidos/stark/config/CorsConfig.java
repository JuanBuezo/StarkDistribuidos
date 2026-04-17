package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * ✅ SECURITY FIX: CORS Configuration - Restrict to specific origins
 * 
 * Vulnerabilities fixed:
 * - CORS was open to all origins (*)
 * - Now restricted to specific whitelisted origins only
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ ONLY ALLOWED ORIGINS
        // Update these URLs for your environment
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // Local development
            "http://localhost:8080",      // Local API
            "http://localhost:8081",      // Local Auth service
            "https://yourdomain.com"      // TODO: Update to your production domain
        ));
        
        // ✅ ALLOWED HTTP METHODS
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // ✅ ALLOWED HEADERS
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "X-Requested-With"
        ));
        
        // ✅ ALLOW CREDENTIALS
        configuration.setAllowCredentials(true);
        
        // ✅ CACHE PREFLIGHT REQUESTS (1 hour)
        configuration.setMaxAge(3600L);
        
        // ✅ EXPOSE HEADERS
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "X-Total-Count"
        ));
        
        // ✅ REGISTER CORS CONFIGURATION
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/auth/**", configuration);
        source.registerCorsConfiguration("/stark-security/**", configuration);
        
        return source;
    }
}

