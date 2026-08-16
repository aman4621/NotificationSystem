package com.project.notification.event;


import com.project.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private Long userId;

    private String title;

    private String message;

    private NotificationType type;
    private boolean isRead;
}
