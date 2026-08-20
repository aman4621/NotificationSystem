package com.project.notification.client;

import com.aman.projectframework.api.APIResponse;
import com.project.notification.response.NotificationProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "iam-service",
        url = "${iam.service.url}"
)
public interface IAMClient {
    @GetMapping("/api/users/notification-profile")
    APIResponse< NotificationProfileResponse> getNotificationProfile(
            @RequestParam Long userId
    );
}
