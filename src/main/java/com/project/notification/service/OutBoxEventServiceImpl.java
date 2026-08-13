package com.project.notification.service;

import com.project.notification.event.OutboxEvent;
import com.project.notification.event.OutboxStatus;
import com.project.notification.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutBoxEventServiceImpl {
    private final OutboxEventRepository outboxEventRepository;
    public List<OutboxEvent> findPendingEventsByUserId(Long userId) {
        return outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
    }
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }
}
