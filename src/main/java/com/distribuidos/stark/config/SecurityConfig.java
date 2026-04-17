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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * ✅ SECURITY FIX: Enhanced Security Configuration for Stark Industries
 * 
 * Vulnerabilities fixed:
 * - Enabled CSRF protection (was disabled)
 * - Added security headers (X-Frame-Options, CSP, etc.)
 * - Configured proper frame options (was disabled - clickjacking risk)
 * - Added method-level security
 * - Integrated CORS configuration
 * - Stronger password requirements
 * 
 * @author Security Team
 * @version 2.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * ✅ SECURITY FIX: Enhanced HTTP Security Filter Chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ SECURITY FIX: CSRF Protection enabled
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/h2-console/**", "/actuator/**")
            )
            // ✅ SECURITY FIX: CORS Configuration
            .cors(cors -> cors.configurationSource(
                request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(java.util.Arrays.asList(
                        "http://localhost:3000",
                        "http://localhost:8080",
                        "http://localhost:8081"
                    ));
                    config.setAllowedMethods(java.util.Arrays.asList("*"));
                    config.setAllowedHeaders(java.util.Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    return config;
                }
            ))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/stark-security/public/**").permitAll()
                // Public static resources
                .requestMatchers("/stark-security/", "/stark-security/index.html").permitAll()
                .requestMatchers("/stark-security/static/**").permitAll()
                .requestMatchers("/stark-security/js/**").permitAll()
                .requestMatchers("/stark-security/styles/**").permitAll()
                .requestMatchers("/stark-security/images/**").permitAll()
                .requestMatchers("/stark-security/css/**").permitAll()
                // WebSocket notifications
                .requestMatchers("/stark-security/ws/**").permitAll()
                // Health checks
                .requestMatchers("/stark-security/api/system/health").permitAll()
                .requestMatchers("/stark-security/api/system/status").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Protected endpoints
                .requestMatchers("/stark-security/api/sensors/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/api/alerts/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/api/access/**").hasAnyRole("ADMIN", "SECURITY", "USER")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            // ✅ SECURITY FIX: Enhanced Security Headers
            .headers(headers -> headers
                // ✅ SECURITY FIX: Frame options set to SAMEORIGIN (was disabled)
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                // ✅ SECURITY FIX: XSS Protection
                .xssProtection()
                .and()
                // ✅ SECURITY FIX: Content Security Policy
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                .and()
                // ✅ SECURITY FIX: Prevent MIME type sniffing
                .contentTypeOptions()
            );

        return http.build();
    }

    /**
     * ✅ SECURITY FIX: User Details Service (Development)
     * TODO: Replace with database-backed UserDetailsService in production
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("Admin@Secure2024!"))  // ✅ SECURITY FIX: Stronger password
            .roles("ADMIN", "SECURITY", "USER")
            .build();

        UserDetails security = User.builder()
            .username("security")
            .password(passwordEncoder().encode("Security@Secure2024!"))
            .roles("SECURITY", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("User@Secure2024!"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, security, user);
    }

    /**
     * ✅ SECURITY FIX: BCrypt Password Encoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // ✅ Increased strength from default
    }
}

