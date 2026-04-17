package com.distribuidos.stark.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ TEST SUITE - ServiceAgent Base Class
 * 
 * Valida:
 * • Inicialización del agente
 * • Manejo de comandos
 * • Manejo de eventos
 * • Publicación de eventos
 * • State management
 * • Health checks
 */
@DisplayName("ServiceAgent Tests")
public class ServiceAgentTest {
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private ObjectMapper objectMapper;
    private TestServiceAgent testAgent;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        testAgent = new TestServiceAgent();
        testAgent.setRabbitTemplate(rabbitTemplate);
        testAgent.setKafkaTemplate(kafkaTemplate);
        testAgent.setObjectMapper(objectMapper);
    }
    
    @Test
    @DisplayName("Should initialize agent successfully")
    public void testAgentInitialization() {
        // Act
        testAgent.initialize();
        
        // Assert
        assertEquals(ServiceAgent.AgentStatus.READY, testAgent.status);
        assertNotNull(testAgent.getState("service_name"));
        assertNotNull(testAgent.getState("initialized_at"));
    }
    
    @Test
    @DisplayName("Should process command successfully")
    public void testHandleCommand() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.CommandMessage command = new ServiceAgent.CommandMessage();
        command.setCommandType("TEST_COMMAND");
        Map<String, Object> payload = new HashMap<>();
        payload.put("test", "value");
        command.setPayload(payload);
        
        // Act
        testAgent.handleCommand(command);
        
        // Assert
        assertTrue(testAgent.getState("command_processed") != null);
        verify(kafkaTemplate, times(1)).send(eq("audit.log"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle command error gracefully")
    public void testHandleCommandError() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.CommandMessage command = new ServiceAgent.CommandMessage();
        command.setCommandType("ERROR_COMMAND");
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> testAgent.handleCommand(command));
        verify(kafkaTemplate, times(1)).send(eq("audit.log"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle event successfully")
    public void testHandleEvent() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.EventMessage event = new ServiceAgent.EventMessage();
        event.setEventType("TEST_EVENT");
        
        // Act
        testAgent.handleEvent(event);
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("audit.log"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should publish command to RabbitMQ")
    public void testPublishCommand() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.CommandMessage command = new ServiceAgent.CommandMessage();
        command.setCommandType("PUBLISH_TEST");
        
        // Act
        testAgent.publishCommand("test.queue", command);
        
        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(eq("test.queue"), anyString());
    }
    
    @Test
    @DisplayName("Should publish event to Kafka")
    public void testPublishEvent() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.EventMessage event = new ServiceAgent.EventMessage();
        event.setEventType("PUBLISH_TEST");
        
        // Act
        testAgent.publishEvent("test.topic", event);
        
        // Assert
        verify(kafkaTemplate, times(1)).send(eq("test.topic"), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should update state correctly")
    public void testUpdateState() {
        // Act
        testAgent.updateState("test_key", "test_value");
        
        // Assert
        assertEquals("test_value", testAgent.getState("test_key"));
    }
    
    @Test
    @DisplayName("Should return health status")
    public void testGetHealth() {
        // Arrange
        testAgent.initialize();
        
        // Act
        Map<String, Object> health = testAgent.getHealth();
        
        // Assert
        assertNotNull(health);
        assertEquals(ServiceAgent.AgentStatus.READY.name(), health.get("status"));
        assertNotNull(health.get("last_heartbeat"));
    }
    
    @Test
    @DisplayName("Should handle null payload gracefully")
    public void testNullPayloadHandling() {
        // Arrange
        testAgent.initialize();
        ServiceAgent.CommandMessage command = new ServiceAgent.CommandMessage();
        command.setCommandType("NULL_TEST");
        command.setPayload(null);
        
        // Act & Assert
        assertDoesNotThrow(() -> testAgent.handleCommand(command));
    }
    
    // ==================== Test Implementation ====================
    
    private static class TestServiceAgent extends ServiceAgent {
        private boolean commandProcessed = false;
        
        @Override
        protected void onInitialize() {
            updateState("test_initialized", true);
        }
        
        @Override
        protected void processCommand(CommandMessage command) throws Exception {
            if ("ERROR_COMMAND".equals(command.getCommandType())) {
                throw new Exception("Test error");
            }
            commandProcessed = true;
            updateState("command_processed", true);
        }
        
        @Override
        protected void processEvent(EventMessage event) throws Exception {
            // Do nothing for test
        }
        
        public void setRabbitTemplate(RabbitTemplate template) {
            this.rabbitTemplate = template;
        }
        
        public void setKafkaTemplate(KafkaTemplate<String, String> template) {
            this.kafkaTemplate = template;
        }
        
        public void setObjectMapper(ObjectMapper mapper) {
            this.objectMapper = mapper;
        }
    }
}

