package com.distribuidos.stark.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body(new TestResponse("Auth Service is running!", "OK"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN REQUEST: username=" + request.username);
        
        if (request.username == null || request.password == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Username and password required"));
        }
        
        // Credenciales por defecto
        if ("admin".equals(request.username) && "admin123".equals(request.password)) {
            LoginResponse response = new LoginResponse(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMTI3MDAwMCwiZXhwIjoxNzExMzU2NDAwfQ.test_token_" + System.currentTimeMillis(),
                request.username,
                86400000L
            );
            System.out.println("LOGIN SUCCESS for: " + request.username);
            return ResponseEntity.ok(response);
        }
        
        System.out.println("LOGIN FAILED for: " + request.username);
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


