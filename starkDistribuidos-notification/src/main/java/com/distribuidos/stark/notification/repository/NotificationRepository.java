package com.distribuidos.stark.notification.repository;

import com.distribuidos.stark.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}