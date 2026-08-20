package com.project.notification.response;

import lombok.Data;

@Data
public class NotificationProfileResponse {
    private Long userId;

    private String email;

    private String phoneNumber;

    private boolean emailEnabled;

    private boolean smsEnabled;

    private boolean pushEnabled;
}
