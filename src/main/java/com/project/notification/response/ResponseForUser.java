package com.project.notification.response;


import com.project.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseForUser {
    private String title;

    private String message;

    private NotificationType type;
}
