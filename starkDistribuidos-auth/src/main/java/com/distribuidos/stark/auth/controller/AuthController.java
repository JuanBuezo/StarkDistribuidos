package com.distribuidos.stark.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ SECURITY FIX: Auth Controller without insecure CORS configuration
 * 
 * Vulnerabilities fixed:
 * - Removed @CrossOrigin(origins = "*") annotation
 * - CORS now configured globally in CorsConfig
 * - Removed credential logging
 * 
 * @author Security Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/auth")
// ✅ SECURITY FIX: CORS removed from here, configured in CorsConfig instead
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("Auth service health check");
        return ResponseEntity.ok().body(new TestResponse("Auth Service is running!", "OK"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // ✅ SECURITY FIX: No credential logging
        log.info("Login attempt");
        
        if (request.username == null || request.password == null) {
            log.warn("Login attempt with missing credentials");
            return ResponseEntity.badRequest().body(new ErrorResponse("Username and password required"));
        }
        
        // Credenciales por defecto (temporal - será reemplazado con BD y JWT)
        if ("admin".equals(request.username) && "Admin@Secure2024!".equals(request.password)) {
            // ✅ SECURITY FIX: Stronger passwords
            LoginResponse response = new LoginResponse(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMTI3MDAwMCwiZXhwIjoxNzExMzU2NDAwfQ.test_token_" + System.currentTimeMillis(),
                request.username,
                86400000L
            );
            log.info("Login successful");
            return ResponseEntity.ok(response);
        }
        
        log.info("Login failed: invalid credentials");
        return ResponseEntity.status(401).body(new ErrorResponse("Invalid credentials"));
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        return ResponseEntity.ok(true);
    }

    // Inner classes
    static class TestResponse {
        public String message;
        public String status;
        
        public TestResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class LoginResponse {
        public String token;
        public String username;
        public Long expiresIn;
        
        public LoginResponse(String token, String username, Long expiresIn) {
            this.token = token;
            this.username = username;
            this.expiresIn = expiresIn;
        }
    }

    static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}


