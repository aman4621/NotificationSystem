package com.project.notification.notificationImplementation;

public interface SmsProvider {
    void send(
            String phoneNumber,
            String message
    );
}
