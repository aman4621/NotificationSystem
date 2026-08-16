package com.project.notification.service;

import com.project.notification.event.InboxEvent;
import com.project.notification.event.InboxEventStatus;
import com.project.notification.repository.InboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxEventService {
    private final InboxEventRepository inboxEventRepository;

    /**
     * Check if event already processed (idempotency check)
     *
     * HOW IT WORKS:
     * 1. Query inbox table for this eventId
     * 2. If found with status PROCESSED → Return existing
     * 3. If found with status PROCESSING → Another thread processing, wait/skip
     * 4. If NOT found → This is new event
     *
     * WHY: Prevents duplicate processing
     */
    @Transactional
    public Optional<InboxEvent> findByEventId(String eventId) {
        log.debug("Checking if eventId {} already processed", eventId);
        return inboxEventRepository.findByEventId(eventId);
    }

    /**
     * Mark event as PROCESSING
     *
     * WHY NEEDED:
     * - In distributed system, same message can be delivered to multiple consumer instances
     * - PROCESSING status means "I'm handling this, don't process again"
     * - If both instances see RECEIVED, both might process! (RACE CONDITION)
     *
     * HOW TO USE:
     * 1. Create inbox event with RECEIVED status
     * 2. Immediately update to PROCESSING
     * 3. If update fails (duplicate constraint), another thread won or message arrived twice
     */
    @Transactional
    public InboxEvent createInboxEvent(
            String eventId,
            Long userId,
            String title,
            String message,
            String type
            ) {

        InboxEvent inboxEvent = InboxEvent.builder()
                .eventId(eventId)
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .status(InboxEventStatus.RECEIVED)  // Start as received
                .createdAt(LocalDateTime.now())
                .retryCount(0)
                .build();

        return inboxEventRepository.save(inboxEvent);
    }

    /**
     * Mark event as currently processing
     */
    @Transactional
    public void markAsProcessing(String eventId) {
        inboxEventRepository.findByEventId(eventId)
                .ifPresent(event -> {
                    event.setStatus(InboxEventStatus.PROCESSING);
                    inboxEventRepository.save(event);
                    log.info("Marked eventId {} as PROCESSING", eventId);
                });
    }

    /**
     * Mark event as successfully processed
     *
     * WHY: After business logic succeeds, update inbox table
     * This signals "don't process again even if message comes again"
     */
    @Transactional
    public void markAsProcessed(String eventId) {
        inboxEventRepository.findByEventId(eventId)
                .ifPresent(event -> {
                    event.setStatus(InboxEventStatus.PROCESSED);
                    event.setProcessedAt(LocalDateTime.now());
                    inboxEventRepository.save(event);
                    log.info("Marked eventId {} as PROCESSED", eventId);
                });
    }

    /**
     * Mark event as failed
     */
    @Transactional
    public void markAsFailed(String eventId, String failureReason) {
        inboxEventRepository.findByEventId(eventId)
                .ifPresent(event -> {
                    event.setStatus(InboxEventStatus.FAILED);
                    event.setFailureReason(failureReason);
                    event.setRetryCount((event.getRetryCount() != null ? event.getRetryCount() : 0) + 1);
                    inboxEventRepository.save(event);
                    log.error("Marked eventId {} as FAILED: {}", eventId, failureReason);
                });
    }
}
