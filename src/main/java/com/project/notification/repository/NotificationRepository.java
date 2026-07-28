package com.project.notification.repository;

import com.aman.projectframework.repository.BaseRepository;
import com.project.notification.entity.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification,Long> {
        List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
