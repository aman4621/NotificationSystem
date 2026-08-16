package com.project.notification.event;

public enum InboxEventStatus {
    RECEIVED,      // Event just arrived, not processed yet
    PROCESSING,    // Currently being processed
    PROCESSED,     // Successfully processed
    FAILED         // Failed after retries
}
