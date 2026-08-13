package com.project.notification.transformer;


import com.project.notification.entity.Notification;
import com.project.notification.event.NotificationEvent;
import com.project.notification.response.NotificationResponse;
import com.project.notification.response.ResponseForUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationTransformer {

    public NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .build();
    }
//    public NotificationResponse toNotificationResponse(NotificationEvent event) {
//        return NotificationResponse.builder()
//                .userId(event.getUserId())
//                .title(event.getTitle())
//                .message(event.getMessage())
//                .type(event.getType())
//                .isRead(event.isRead())
//                .build();
//    }

    public List<ResponseForUser> toResponseForUser(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> ResponseForUser.builder()
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .build())
                .toList();
    }
}
