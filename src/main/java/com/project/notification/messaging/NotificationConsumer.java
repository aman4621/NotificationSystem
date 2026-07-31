package com.project.notification.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationConsumer {
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        log.info("Consumed message: " + message);
    }
}
