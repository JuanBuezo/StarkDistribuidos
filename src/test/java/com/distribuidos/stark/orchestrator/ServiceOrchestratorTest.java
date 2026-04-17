package com.distribuidos.stark.orchestrator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ TEST SUITE - ServiceOrchestrator
 * 
 * Valida:
 * • Registración de servicios
 * • Enrutamiento de comandos
 * • Orquestación de flujos
 * • Circuit breaker
 * • Health monitoring
 */
@DisplayName("ServiceOrchestrator Tests")
public class ServiceOrchestratorTest {
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private ObjectMapper objectMapper;
    private ServiceOrchestrator orchestrator;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        orchestrator = new ServiceOrchestrator();
        orchestrator.rabbitTemplate = rabbitTemplate;
        orchestrator.kafkaTemplate = kafkaTemplate;
        orchestrator.objectMapper = objectMapper;
    }
    
    @Test
    @DisplayName("Should register service successfully")
    public void testRegisterService() {
        // Act
        orchestrator.registerService("test-service", "test.q", "test.topic");
        
        // Assert
        Map<String, Object> status = orchestrator.getOrchestratorStatus();
        assertEquals(1, status.get("registered_services"));
    }
    
    @Test
    @DisplayName("Should send command to registered service")
    public void testSendCommand() {
        // Arrange
        orchestrator.registerService("auth-service", "auth.command.q", "auth.events");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("password", "test");
        
        // Act
        orchestrator.sendCommand("auth-service", "LOGIN", payload);
        
        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(eq("auth.command.q"), anyString());
        verify(kafkaTemplate, times(1)).send(eq("orchestrator.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle unregistered service")
    public void testSendCommandToUnregisteredService() {
        // Act - Should not throw exception
        assertDoesNotThrow(() -> {
            orchestrator.sendCommand("unknown-service", "COMMAND", new HashMap<>());
        });
    }
    
    @Test
    @DisplayName("Should orchestrate multi-step workflow")
    public void testOrchestrateWorkflow() {
        // Arrange
        orchestrator.registerService("auth-service", "auth.command.q", "auth.events");
        orchestrator.registerService("access-service", "access.command.q", "access.events");
        
        List<ServiceOrchestrator.WorkflowStep> steps = new ArrayList<>();
        steps.add(new ServiceOrchestrator.WorkflowStep("step1", "auth-service", "LOGIN", new HashMap<>()));
        steps.add(new ServiceOrchestrator.WorkflowStep("step2", "access-service", "CHECK_ACCESS", new HashMap<>()));
        
        // Act
        orchestrator.orchestrateWorkflow("workflow-1", steps);
        
        // Assert
        verify(rabbitTemplate, atLeast(2)).convertAndSend(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should record success in circuit breaker")
    public void testCircuitBreakerSuccess() {
        // Arrange
        orchestrator.registerService("test-service", "test.q", "test.topic");
        
        // Act
        orchestrator.sendCommand("test-service", "TEST", new HashMap<>());
        
        // Assert
        Map<String, Object> status = orchestrator.getOrchestratorStatus();
        Map<String, String> cbStatus = (Map<String, String>) status.get("circuit_breakers");
        assertEquals("CLOSED", cbStatus.get("test-service"));
    }
    
    @Test
    @DisplayName("Should handle multiple services")
    public void testMultipleServices() {
        // Arrange
        String[] services = {"auth", "access", "sensor", "alert", "notification"};
        
        // Act
        for (String service : services) {
            orchestrator.registerService(service + "-service", service + ".q", service + ".topic");
        }
        
        // Assert
        Map<String, Object> status = orchestrator.getOrchestratorStatus();
        assertEquals(5, status.get("registered_services"));
    }
    
    @Test
    @DisplayName("Should publish audit events")
    public void testAuditLogging() {
        // Arrange
        orchestrator.registerService("test-service", "test.q", "test.topic");
        
        // Act
        orchestrator.sendCommand("test-service", "TEST", new HashMap<>());
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("orchestrator.events"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should get orchestrator status")
    public void testGetOrchestratorStatus() {
        // Arrange
        orchestrator.registerService("service-1", "queue-1", "topic-1");
        orchestrator.registerService("service-2", "queue-2", "topic-2");
        
        // Act
        Map<String, Object> status = orchestrator.getOrchestratorStatus();
        
        // Assert
        assertNotNull(status);
        assertNotNull(status.get("timestamp"));
        assertEquals(2, status.get("registered_services"));
        assertNotNull(status.get("circuit_breakers"));
    }
}

