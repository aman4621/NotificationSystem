package com.project.notification.service;

import com.project.notification.entity.FailedNotification;
import com.project.notification.repository.FailedNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedNotificationServiceImpl {
    private final FailedNotificationRepository failedNotificationRepository;
    public FailedNotification saveFailedNotification(FailedNotification failedNotification) {
        failedNotificationRepository.save(failedNotification);
        return failedNotification;
    }
}
