package com.distribuidos.stark.notification.service;

import com.distribuidos.stark.notification.model.Notification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotificationEmail(String to, Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(notification.getTitle());
        message.setText(notification.getMessage());

        mailSender.send(message);
    }
}