package com.project.notification.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "inbox_event",
        // CRITICAL: This index ensures fast duplicate detection
        // Why: When message arrives, we query "WHERE event_id = ?", this must be fast
        indexes = {
                @Index(name = "idx_event_id", columnList = "event_id", unique = true),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UNIQUE constraint: Each eventId can only appear once
    // This prevents even trying to insert duplicate
    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InboxEventStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    private Integer retryCount;

    private String failureReason;
}
