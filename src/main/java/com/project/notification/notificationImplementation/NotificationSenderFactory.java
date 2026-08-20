package com.project.notification.notificationImplementation;

import com.project.notification.entity.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class NotificationSenderFactory {
    private final Map<NotificationType, NotificationSender> senders;

    public NotificationSenderFactory(
            List<NotificationSender> senderList) {

        this.senders = senderList.stream()
                .collect(Collectors.toMap(
                        NotificationSender::getType,
                        Function.identity()
                ));
    }

    public NotificationSender getSender(
            NotificationType type) {

        NotificationSender sender = senders.get(type);

        if (sender == null) {
            throw new IllegalArgumentException(
                    "No notification sender configured for "
                            + type
            );
        }

        return sender;
    }
}
