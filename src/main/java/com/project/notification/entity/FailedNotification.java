package com.project.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "failed_notification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FailedNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    private String originalTopic;

    private Integer originalPartition;

    private Long originalOffset;

    private LocalDateTime failedAt;
}
