package com.project.notification.controller;

import com.aman.projectframework.api.APIResponse;
import com.project.notification.entity.NotificationType;
import com.project.notification.event.NotificationEvent;
import com.project.notification.facade.NotificationFacade;
import com.project.notification.messaging.NotificationProducer;
import com.project.notification.request.NotificationRequest;
import com.project.notification.response.NotificationResponse;
import com.project.notification.response.ResponseForUser;
import com.project.notification.response.ResponseFromId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/aman")
@RequiredArgsConstructor
@Validated
public class NotificationController {
    private final NotificationFacade facade;
    private final NotificationProducer producer;
    @PostMapping("/notification")
    public ResponseEntity<APIResponse<NotificationResponse>> createNotification(@Valid @RequestBody NotificationRequest request){
        NotificationResponse result=facade.makeNotification(request);
        APIResponse<NotificationResponse> response=APIResponse.<NotificationResponse>builder()
                .msId("Notify")
                .status("Success")
                .httpStstus(201)
                .message("Notification created successfully")
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<APIResponse<List<ResponseForUser>>> getNotificationForUser(@PathVariable  @Positive @Min(1) long userId){
        List<ResponseForUser> result=facade.getNotificationUser(userId);
        APIResponse<List<ResponseForUser>> response=APIResponse.<List<ResponseForUser>>builder()
                .msId("notify")
                .status("success")
                .httpStstus(200)
                .message("Notification found for the user")
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    @GetMapping("/notify/{id}")
    public ResponseEntity<APIResponse<ResponseFromId>> getNotificationFromId(@PathVariable @Positive long id){
        ResponseFromId result=facade.getNotificationId(id);
        APIResponse<ResponseFromId> response=APIResponse.<ResponseFromId>builder()
                .msId("Notify")
                .status("Success")
                .httpStstus(200)
                .message("Notification fetched successfully for the user")
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id) {

        facade.markAsRead(id);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/test")
    public String test() {

        NotificationEvent event =
                NotificationEvent.builder()
                        .userId(101L)
                        .title("Test")
                        .message("Kafka Test")
                        .type(NotificationType.EMAIL)
                        .build();

        producer.send(event);

        return "Message Sent";
    }
}
