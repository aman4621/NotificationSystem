package com.project.notification.notificationImplementation;

import com.project.notification.entity.NotificationType;
import com.project.notification.event.NotificationEvent;
import com.project.notification.response.NotificationProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class SmsNotificationSender implements NotificationSender {
    private final SmsProvider smsProvider;

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(
            NotificationEvent event,
            NotificationProfileResponse profile) {

        if (!profile.isSmsEnabled()) {
            log.info(
                    "SMS notification disabled for user {}",
                    event.getUserId()
            );
            return;
        }

        if (profile.getPhoneNumber() == null ||
                profile.getPhoneNumber().isBlank()) {

            throw new RuntimeException(
                    "Phone number not available for user "
                            + event.getUserId()
            );
        }

        smsProvider.send(
                profile.getPhoneNumber(),
                event.getMessage()
        );

        log.info(
                "SMS notification sent to user {}",
                event.getUserId()
        );
    }
}
