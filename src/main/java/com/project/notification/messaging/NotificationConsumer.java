package com.project.notification.messaging;

import com.aman.projectframework.exception.ServiceException;
import com.project.notification.entity.FailedNotification;
import com.project.notification.entity.Notification;
import com.project.notification.event.NotificationEvent;
import com.project.notification.repository.FailedNotificationRepository;
import com.project.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final FailedNotificationRepository failedNotificationRepository;
    @RetryableTopic(
            attempts = "4",
            backoff=@Backoff(delay = 5000, multiplier = 6,maxDelay = 120000),
            retryTopicSuffix = "-retry",
            dltTopicSuffix = "-dlt",
            numPartitions = "3"
    )
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        try {
            Notification notification = Notification.builder()
                    .userId(event.getUserId())
                    .title(event.getTitle())
                    .message(event.getMessage())
                    .type(event.getType())
                    .isRead(false)
                    .build();
            notificationService.makeNotification(notification);
            log.info("notification saved successfully");
        } catch (Exception e) {
            log.error("error occurred while saving notification", e);
            throw new ServiceException(
                    "ERR123",
                    e.getMessage(),
                    "ct123",
                    "sv123",
                    null
            );
        }
//        log.info("Received Notification Event {}", event);
//        throw new RuntimeException("Simulated exception for testing DLT handling");
    }
    @DltHandler
    public void handleDlt(NotificationEvent event,
                          @Header(value = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false)
                          String exceptionMessage,

                          @Header(value = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false)
                              String originalTopic,

                          @Header(value = KafkaHeaders.DLT_ORIGINAL_PARTITION, required = false)
                              Integer originalPartition,

                          @Header(value = KafkaHeaders.DLT_ORIGINAL_OFFSET, required = false)
                              Long originalOffset) {
        try {

            log.info("Handling DLT for notification event: {}", event);

            FailedNotification failedNotification = FailedNotification.builder()
                    .userId(event.getUserId())
                    .title(event.getTitle())
                    .message(event.getMessage())
                    .type(event.getType())
                    .failureReason(exceptionMessage != null ? exceptionMessage : "Unknown error")
                    .originalTopic(originalTopic != null ? originalTopic : "unknown")
                    .originalPartition(originalPartition != null ? originalPartition : -1)
                    .originalOffset(originalOffset != null ? originalOffset : -1L)
                    .failedAt(LocalDateTime.now())
                    .build();
            failedNotificationRepository.save(failedNotification);
        } catch (Exception e) {
            log.error("Error occurred while handling DLT for notification event: {}", event, e);
            throw new ServiceException(
                    "ERR456",
                    e.getMessage(),
                    "ct456",
                    "sv456",
                    null
            );

        }
    }
}
