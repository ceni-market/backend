package com.cenimarket.backend.notification.repository;

import com.cenimarket.backend.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
