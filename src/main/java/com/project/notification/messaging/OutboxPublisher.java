package com.project.notification.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notification.event.NotificationEvent;
import com.project.notification.event.OutboxEvent;
import com.project.notification.event.OutboxStatus;
import com.project.notification.service.OutBoxEventServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {
    private final OutBoxEventServiceImpl outBoxEventService;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Scheduled(fixedDelay = 1000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outBoxEventService.findPendingEventsByUserId(
                        OutboxStatus.PENDING
                );

        for (OutboxEvent outboxEvent : events) {

            try {

                NotificationEvent event =
                        objectMapper.readValue(
                                outboxEvent.getPayload(),
                                NotificationEvent.class
                        );

                kafkaTemplate.send(
                        "notification-topic",
                        outboxEvent.getAggregateId(),
                        event
                ).whenComplete((result, exception) -> {

                    if (exception == null) {

                        outboxEvent.setStatus(
                                OutboxStatus.PUBLISHED
                        );

                        outboxEvent.setPublishedAt(
                                LocalDateTime.now()
                        );

                        outBoxEventService.save(outboxEvent);

                        log.info(
                                "Outbox event {} published successfully",
                                outboxEvent.getId()
                        );

                    } else {

                        outboxEvent.setRetryCount(
                                outboxEvent.getRetryCount() + 1
                        );

                        outBoxEventService.save(outboxEvent);

                        log.error(
                                "Failed to publish outbox event {}",
                                outboxEvent.getId(),
                                exception
                        );
                    }
                });

            } catch (JsonProcessingException e) {

                outboxEvent.setStatus(OutboxStatus.FAILED);

                outBoxEventService.save(outboxEvent);

                log.error(
                        "Failed to deserialize outbox event {}",
                        outboxEvent.getId(),
                        e
                );
            }
        }
    }
}
