package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el sistema de seguridad de Stark Industries.
 * 
 * Define:
 * - Autenticación de usuarios
 * - Control de acceso basado en roles
 * - Encriptación de contraseñas
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/stark-security/public/**").permitAll()
                .requestMatchers("/stark-security/sensors/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/alerts/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/access/**").hasAnyRole("ADMIN", "SECURITY", "USER")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );

        return http.build();
    }

    /**
     * Define usuarios en memoria para desarrollo.
     * En producción, estos vendrían de una base de datos.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "SECURITY", "USER")
            .build();

        UserDetails security = User.builder()
            .username("security")
            .password(passwordEncoder().encode("security123"))
            .roles("SECURITY", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, security, user);
    }

    /**
     * Configura el codificador de contraseñas usando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

