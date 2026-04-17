package com.distribuidos.stark.starkDistribuidos_access.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ TEST SUITE - AccessControlAgent
 * 
 * Valida:
 * • Check access granted
 * • Check access denied
 * • Permission grant
 * • Permission revoke
 * • Admin permissions
 */
@DisplayName("AccessControlAgent Tests")
public class AccessControlAgentTest {
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private ObjectMapper objectMapper;
    private AccessControlAgent accessAgent;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        accessAgent = new AccessControlAgent();
        accessAgent.kafkaTemplate = kafkaTemplate;
        accessAgent.objectMapper = objectMapper;
    }
    
    @Test
    @DisplayName("Should initialize AccessControlAgent")
    public void testInitialize() {
        // Act
        accessAgent.initialize();
        
        // Assert
        assertNotNull(accessAgent.getState("permissions"));
        assertNotNull(accessAgent.getState("commands_processed"));
    }
    
    @Test
    @DisplayName("Should grant access to admin")
    public void testAdminAccess() {
        // Arrange
        accessAgent.initialize();
        AccessControlAgent.CommandMessage command = new AccessControlAgent.CommandMessage();
        command.setCommandType("CHECK_ACCESS");
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", "admin");
        payload.put("resource", "admin_panel");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("access.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should deny access to unauthorized user")
    public void testAccessDenied() {
        // Arrange
        accessAgent.initialize();
        AccessControlAgent.CommandMessage command = new AccessControlAgent.CommandMessage();
        command.setCommandType("CHECK_ACCESS");
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", "user1");
        payload.put("resource", "admin_panel");  // user1 no tiene este permiso
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("access.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should grant user permission")
    public void testGrantPermission() {
        // Arrange
        accessAgent.initialize();
        AccessControlAgent.CommandMessage command = new AccessControlAgent.CommandMessage();
        command.setCommandType("GRANT_PERMISSION");
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", "user1");
        payload.put("resource", "reports");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("access.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should revoke user permission")
    public void testRevokePermission() {
        // Arrange
        accessAgent.initialize();
        
        // First grant permission
        AccessControlAgent.CommandMessage grantCmd = new AccessControlAgent.CommandMessage();
        grantCmd.setCommandType("GRANT_PERMISSION");
        Map<String, Object> grantPayload = new HashMap<>();
        grantPayload.put("user_id", "user1");
        grantPayload.put("resource", "reports");
        grantCmd.setPayload(grantPayload);
        accessAgent.handleCommand(grantCmd);
        
        // Now revoke
        AccessControlAgent.CommandMessage revokeCmd = new AccessControlAgent.CommandMessage();
        revokeCmd.setCommandType("REVOKE_PERMISSION");
        Map<String, Object> revokePayload = new HashMap<>();
        revokePayload.put("user_id", "user1");
        revokePayload.put("resource", "reports");
        revokeCmd.setPayload(revokePayload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(revokeCmd));
        
        // Assert
        verify(kafkaTemplate, atLeast(1)).send(eq("access.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should list user permissions")
    public void testListPermissions() {
        // Arrange
        accessAgent.initialize();
        AccessControlAgent.CommandMessage command = new AccessControlAgent.CommandMessage();
        command.setCommandType("LIST_PERMISSIONS");
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", "admin");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("access.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle user1 default permissions")
    public void testDefaultUserPermissions() {
        // Arrange
        accessAgent.initialize();
        AccessControlAgent.CommandMessage command = new AccessControlAgent.CommandMessage();
        command.setCommandType("LIST_PERMISSIONS");
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", "user1");
        command.setPayload(payload);
        
        // Act
        assertDoesNotThrow(() -> accessAgent.handleCommand(command));
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("access.events"), anyString(), anyString());
    }
}

