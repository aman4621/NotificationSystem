package com.project.notification.notificationImplementation;

import com.project.notification.entity.NotificationType;
import com.project.notification.event.NotificationEvent;
import com.project.notification.response.NotificationProfileResponse;

public interface NotificationSender {
    void send(
            NotificationEvent event,
            NotificationProfileResponse profile
    );

    NotificationType getType();
}
