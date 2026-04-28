package com.distribuidos.stark.repository;

import com.distribuidos.stark.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}