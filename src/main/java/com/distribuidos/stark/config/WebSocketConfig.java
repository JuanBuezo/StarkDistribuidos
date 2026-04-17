package com.distribuidos.stark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para notificaciones en tiempo real.
 * 
 * Permite:
 * - Comunicación bidireccional entre servidor y clientes
 * - Notificaciones de alertas instantáneas
 * - Actualización de estado de sensores en vivo
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes para enrutar mensajes STOMP.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra los endpoints de WebSocket.
     * Los clientes se conectarán a estos endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/notifications")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000")
                .withSockJS();
        
        registry.addEndpoint("/ws/sensors")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000")
                .withSockJS();
        
        registry.addEndpoint("/ws/alerts")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000")
                .withSockJS();
    }
}

