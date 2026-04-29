package com.distribuidos.stark.notification.controller;

import com.distribuidos.stark.notification.model.Notification;
import com.distribuidos.stark.notification.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/test")
    public String test() {
        return "Notification Service OK";
    }
}