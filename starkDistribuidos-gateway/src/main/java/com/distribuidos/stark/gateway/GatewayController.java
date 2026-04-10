package com.distribuidos.stark.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayController {

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Gateway running!");
        response.put("service", "Stark API Gateway");
        response.put("version", "1.0.0");
        response.put("available_routes", new String[]{
            "/api/sensors/**",
            "/api/alerts/**",
            "/api/access/**",
            "/api/notifications/**",
            "/api/auth/**",
            "/actuator/health"
        });
        return response;
    }

    @GetMapping("/stark")
    public Map<String, Object> stark() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Stark Industries Security System");
        response.put("routes", new String[]{
            "GET /api/sensors - Get all sensors",
            "GET /api/alerts - Get all alerts",
            "GET /api/access - Get access logs",
            "POST /api/auth/login - Login"
        });
        response.put("credentials", "admin / admin123");
        return response;
    }

    @GetMapping("/actuator/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "gateway");
        return response;
    }
}

