package com.project.notification.repository;

import com.project.notification.event.InboxEvent;
import com.project.notification.event.InboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
    // CRITICAL: Check if we've already seen this eventId
    // Why: Returns Optional - if present, we already processed it (skip it)
    Optional<InboxEvent> findByEventId(String eventId);

    // Find failed events to retry later
    List<InboxEvent> findByStatusOrderByCreatedAtAsc(InboxEventStatus status);
}
