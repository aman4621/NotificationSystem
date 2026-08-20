package com.project.notification.notificationImplementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpSmsProvider implements SmsProvider{
    @Override
    public void send(String phoneNumber, String message) {
        log.info("SMS provider not yet implemented. Would send to {}: {}", phoneNumber, message);
    }
}
