package com.distribuidos.stark.notification.controller;

import com.distribuidos.stark.notification.model.Notification;
import com.distribuidos.stark.notification.repository.NotificationRepository;
import com.distribuidos.stark.notification.service.EmailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;

    public NotificationController(
            NotificationRepository notificationRepository,
            EmailNotificationService emailNotificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/user/{recipient}")
    public List<Notification> getNotificationsByRecipient(@PathVariable("recipient") String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/email")
    public ResponseEntity<Notification> createNotificationAndSendEmail(@RequestBody Notification notification) {
        Notification saved = notificationRepository.save(notification);

        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            try {
                emailNotificationService.sendNotificationEmail(saved.getEmail(), saved);
            } catch (Exception e) {
                System.out.println("Could not send email notification: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable("id") Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setRead(true);
                    Notification updated = notificationRepository.save(notification);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        notificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test() {
        return "Notification Service OK";
    }
}