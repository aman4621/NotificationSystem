package com.project.notification.messaging;

import com.aman.projectframework.exception.ServiceException;
import com.project.notification.entity.FailedNotification;
import com.project.notification.entity.Notification;
import com.project.notification.event.InboxEvent;
import com.project.notification.event.NotificationEvent;
import com.project.notification.repository.FailedNotificationRepository;
import com.project.notification.service.InboxEventService;
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
import java.util.Optional;

import static com.project.notification.event.InboxEventStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final FailedNotificationRepository failedNotificationRepository;
    private final InboxEventService inboxEventService;
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
            log.info("Received notification event: eventId={}, userId={}",
                    event.getEventId(), event.getUserId());

            // ====== STEP 1: IDEMPOTENCY CHECK ======
            // WHY: Check if we've already processed this exact event
            // If YES with status PROCESSED → SKIP (don't duplicate)
            // If YES with status PROCESSING → WAIT or SKIP (another thread handling)
            Optional<InboxEvent> existingInboxEvent =
                    inboxEventService.findByEventId(event.getEventId());

            if (existingInboxEvent.isPresent()) {
                InboxEvent inbox = existingInboxEvent.get();

            switch (inbox.getStatus()) {
                case PROCESSED:
                    // This event was already successfully processed
                    // Return silently (idempotent operation)
                    log.info("Event {} already PROCESSED, skipping",
                            event.getEventId());
                    return;

                case PROCESSING:
                    // Another consumer instance is currently processing this
                    // Throw exception so Kafka retries (backoff)
                    log.warn("Event {} currently being PROCESSED by another instance",
                            event.getEventId());
                    throw new RuntimeException(
                            "Event still being processed by another instance"
                    );

                case FAILED:
                    // Event previously failed
                    log.warn("Event {} previously FAILED, attempting retry",
                            event.getEventId());
                    // Continue to process (retry logic)
                    break;
            }
        }

            // ====== STEP 2: CREATE INBOX ENTRY ======
            // WHY: Log this event in inbox table before processing
            // This prevents race conditions if another instance gets same message
            inboxEventService.createInboxEvent(
                    event.getEventId(),
                    event.getUserId(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getType().toString()
            );


            // ====== STEP 3: MARK AS PROCESSING ======
            // WHY: Signal that we're now handling this event
            inboxEventService.markAsProcessing(event.getEventId());

            // ====== STEP 4: PROCESS BUSINESS LOGIC ======
            // WHY: Create actual notification record
            Notification notification = Notification.builder()
                    .userId(event.getUserId())
                    .title(event.getTitle())
                    .message(event.getMessage())
                    .type(event.getType())
                    .isRead(false)
                    .build();
            notificationService.makeNotification(notification);
            log.info("notification saved successfully");

            // ====== STEP 5: MARK AS PROCESSED ======
            // WHY: Signal "don't process again", confirm to Kafka offset is safe to commit
            inboxEventService.markAsProcessed(event.getEventId());
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
// Mark inbox event as failed (final attempt)
            inboxEventService.markAsFailed(
                    event.getEventId(),
                    "Exhausted all retries. DLT: " + exceptionMessage
            );
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
