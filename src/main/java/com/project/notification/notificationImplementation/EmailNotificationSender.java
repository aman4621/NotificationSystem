package com.project.notification.notificationImplementation;

import com.project.notification.entity.NotificationType;
import com.project.notification.event.NotificationEvent;
import com.project.notification.response.NotificationProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class EmailNotificationSender implements NotificationSender {
    private final JavaMailSender mailSender;

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    public void send(
            NotificationEvent event,
            NotificationProfileResponse profile) {
        // Implement email sending logic using JavaMailSender
        // For example, create a SimpleMailMessage and send it using mailSender
        if(!profile.isEmailEnabled()){
            log.info(
                    "Email notifications are disabled for this profile.",
                    event.getUserId()
            );
            return;
        }
        if (profile.getEmail() == null ||
                profile.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Email address not available for user "
                            + event.getUserId()
            );
        }

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(profile.getEmail());

        mail.setSubject(event.getTitle());

        mail.setText(event.getMessage());

        mailSender.send(mail);

        log.info(
                "Email notification sent to user {}",
                event.getUserId()
        );

    }
}
