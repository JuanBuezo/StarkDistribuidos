package com.distribuidos.stark.service;

import com.distribuidos.stark.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio de email para notificaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.from:seguridad@starkindustries.com}")
    private String fromEmail;
    
    @Value("${app.email.admin:admin@starkindustries.com}")
    private String adminEmail;
    
    @Async("alertExecutor")
    public void sendCriticalAlert(Alert alert) {
        log.info("Enviando email de alerta crítica: {}", alert.getId());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("⚠️ ALERTA CRÍTICA - Sistema de Seguridad Stark Industries");
            message.setText(buildAlertEmailBody(alert, true));
            
            mailSender.send(message);
            log.info("Email de alerta crítica enviado exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar email de alerta crítica", e);
        }
    }
    
    @Async("alertExecutor")
    public void sendWarningAlert(Alert alert) {
        log.info("Enviando email de advertencia: {}", alert.getId());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("⚠️ ADVERTENCIA - Sistema de Seguridad Stark Industries");
            message.setText(buildAlertEmailBody(alert, false));
            
            mailSender.send(message);
            log.info("Email de advertencia enviado exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar email de advertencia", e);
        }
    }
    
    @Async("alertExecutor")
    public void sendAccessDenialNotification(String username, String sensorName, String timestamp) {
        log.info("Enviando notificación de acceso denegado para usuario: {}", username);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("🚫 ACCESO DENEGADO - " + username);
            message.setText(buildAccessDenialEmailBody(username, sensorName, timestamp));
            
            mailSender.send(message);
            log.info("Email de acceso denegado enviado exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar email de acceso denegado", e);
        }
    }
    
    @Async("alertExecutor")
    public void sendSystemStatusReport(String statusReport) {
        log.info("Enviando reporte de estado del sistema");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("📊 Reporte de Estado del Sistema - Stark Industries");
            message.setText(statusReport);
            
            mailSender.send(message);
            log.info("Reporte de estado enviado exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar reporte de estado", e);
        }
    }
    
    private String buildAlertEmailBody(Alert alert, boolean isCritical) {
        return """
            ═══════════════════════════════════════════════════════════════════
            SISTEMA DE SEGURIDAD STARK INDUSTRIES
            ═══════════════════════════════════════════════════════════════════
            
            ALERTA DE SEGURIDAD CRÍTICA
            
            Nivel de Alerta: %s
            ID de Alerta: %d
            Fecha/Hora: %s
            ═══════════════════════════════════════════════════════════════════
            
            DETALLES DEL SENSOR
            Sensor: %s
            Tipo: %s
            Ubicación: %s
            Estado: %s
            
            MENSAJE DE ALERTA
            %s
            
            DETALLES ADICIONALES
            %s
            
            ═══════════════════════════════════════════════════════════════════
            
            ACCIÓN RECOMENDADA:
            %s
            
            Por favor, inicie sesión en el sistema de administración para más detalles:
            http://localhost:8080/stark-security/
            
            ═══════════════════════════════════════════════════════════════════
            Este es un email automático. No responda a este email.
            """
            .formatted(
                alert.getLevel().getDescription(),
                alert.getId(),
                alert.getCreatedAt(),
                alert.getSensor().getName(),
                alert.getSensor().getType().getDescription(),
                alert.getSensor().getLocation(),
                alert.getSensor().getActive() ? "Activo" : "Inactivo",
                alert.getMessage(),
                alert.getDetails() != null ? alert.getDetails() : "N/A",
                isCritical ? "¡ACCIÓN INMEDIATA REQUERIDA!" : "Revise el sistema cuando sea posible"
            );
    }
    
    private String buildAccessDenialEmailBody(String username, String sensorName, String timestamp) {
        return """
            ═══════════════════════════════════════════════════════════════════
            SISTEMA DE SEGURIDAD STARK INDUSTRIES
            ═══════════════════════════════════════════════════════════════════
            
            INTENTO DE ACCESO DENEGADO
            
            Usuario: %s
            Sensor: %s
            Fecha/Hora: %s
            Estado: ACCESO DENEGADO
            
            ═══════════════════════════════════════════════════════════════════
            
            Este usuario intentó acceder a un área restringida y fue denegado
            por el sistema de seguridad.
            
            Si esto fue un error, contacte al administrador del sistema.
            
            ═══════════════════════════════════════════════════════════════════
            Este es un email automático. No responda a este email.
            """
            .formatted(username, sensorName, timestamp);
    }
}

