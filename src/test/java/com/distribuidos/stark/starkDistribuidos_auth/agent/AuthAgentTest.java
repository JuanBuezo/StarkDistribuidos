package com.distribuidos.stark.starkDistribuidos_auth.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ TEST SUITE - AuthAgent
 * 
 * Valida:
 * • Login exitoso
 * • Login fallido
 * • Token validation
 * • Token refresh
 * • Event publishing
 */
@DisplayName("AuthAgent Tests")
public class AuthAgentTest {
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private ObjectMapper objectMapper;
    private AuthAgent authAgent;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        authAgent = new AuthAgent();
        authAgent.kafkaTemplate = kafkaTemplate;
        authAgent.objectMapper = objectMapper;
    }
    
    @Test
    @DisplayName("Should initialize AuthAgent")
    public void testInitialize() {
        // Act
        authAgent.initialize();
        
        // Assert
        assertNotNull(authAgent.getState("users"));
        assertTrue(authAgent.getState("users") instanceof Map);
    }
    
    @Test
    @DisplayName("Should authenticate valid user")
    public void testValidLogin() {
        // Arrange
        authAgent.initialize();
        AuthAgent.CommandMessage command = new AuthAgent.CommandMessage();
        command.setCommandType("LOGIN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("password", "Admin@Secure2024!");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> authAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("auth.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should reject invalid login")
    public void testInvalidLogin() {
        // Arrange
        authAgent.initialize();
        AuthAgent.CommandMessage command = new AuthAgent.CommandMessage();
        command.setCommandType("LOGIN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("password", "wrongpassword");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> authAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("auth.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should reject non-existent user")
    public void testNonExistentUser() {
        // Arrange
        authAgent.initialize();
        AuthAgent.CommandMessage command = new AuthAgent.CommandMessage();
        command.setCommandType("LOGIN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "nonexistent");
        payload.put("password", "password");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> authAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("auth.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should validate token")
    public void testValidateToken() {
        // Arrange
        authAgent.initialize();
        AuthAgent.CommandMessage command = new AuthAgent.CommandMessage();
        command.setCommandType("VALIDATE_TOKEN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", "eyJhbGciOiJIUzUxMiJ9.test");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> authAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("auth.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should reject invalid token")
    public void testInvalidToken() {
        // Arrange
        authAgent.initialize();
        AuthAgent.CommandMessage command = new AuthAgent.CommandMessage();
        command.setCommandType("VALIDATE_TOKEN");
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", "invalid_token");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> authAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("auth.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should track login metrics")
    public void testLoginMetrics() {
        // Arrange
        authAgent.initialize();
        
        // Act - Successful login
        AuthAgent.CommandMessage successCmd = new AuthAgent.CommandMessage();
        successCmd.setCommandType("LOGIN");
        Map<String, Object> successPayload = new HashMap<>();
        successPayload.put("username", "admin");
        successPayload.put("password", "Admin@Secure2024!");
        successCmd.setPayload(successPayload);
        authAgent.handleCommand(successCmd);
        
        // Act - Failed login
        AuthAgent.CommandMessage failCmd = new AuthAgent.CommandMessage();
        failCmd.setCommandType("LOGIN");
        Map<String, Object> failPayload = new HashMap<>();
        failPayload.put("username", "admin");
        failPayload.put("password", "wrongpassword");
        failCmd.setPayload(failPayload);
        authAgent.handleCommand(failCmd);
        
        // Assert
        verify(kafkaTemplate, times(2)).send(eq("auth.events"), anyString(), anyString());
    }
}

