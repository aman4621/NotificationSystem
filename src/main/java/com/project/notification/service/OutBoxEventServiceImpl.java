package com.project.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notification.event.NotificationEvent;
import com.project.notification.event.OutboxEvent;
import com.project.notification.event.OutboxStatus;
import com.project.notification.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutBoxEventServiceImpl {
    private final OutboxEventRepository outboxEventRepository;
    public List<OutboxEvent> findPendingEventsByUserId(OutboxStatus outboxStatus) {
        return outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(outboxStatus);
    }
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }

    private final ObjectMapper objectMapper;

    @Transactional
    public void saveNotificationEvent(NotificationEvent event) {

        try {
            // Generate unique ID for this event (if not already set)
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }

            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventType("NotificationCreated")
                    .aggregateType("Notification")
                    .aggregateId(event.getUserId().toString())
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize notification event", e
            );
        }
    }
}
