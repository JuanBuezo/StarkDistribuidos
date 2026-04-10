package com.distribuidos.stark.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping
    public Map<String, Object> getNotifications() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("notifications", new Object[]{});
        response.put("service", "notification");
        return response;
    }

    @PostMapping
    public Map<String, Object> sendNotification() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SENT");
        response.put("message", "Notification sent successfully");
        return response;
    }
}

