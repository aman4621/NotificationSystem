package com.project.notification.request;



import com.project.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private NotificationType type;
}
