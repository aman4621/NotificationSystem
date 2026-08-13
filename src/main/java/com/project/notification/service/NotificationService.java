package com.project.notification.service;


import com.project.notification.entity.Notification;
import com.project.notification.event.NotificationEvent;
import com.project.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    public Notification makeNotification(Notification notification) {
        Notification notification1=repository.save(notification);
        return notification1;
    }


    public List<Notification> getNotificationByUserId(long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification getNotificationById(long id) {
        return repository.findById(id).get();
    }

    public void save(Notification notification) {
        repository.save(notification);
    }
}
