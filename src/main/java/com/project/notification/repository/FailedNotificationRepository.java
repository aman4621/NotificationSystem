package com.project.notification.repository;

import com.project.notification.entity.FailedNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedNotificationRepository extends JpaRepository<FailedNotification, Long> {
}
