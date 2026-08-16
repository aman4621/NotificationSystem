package com.project.notification.facade;

import com.aman.projectframework.exception.ServiceException;
import com.project.notification.entity.Notification;
import com.project.notification.event.NotificationEvent;

import com.project.notification.messaging.NotificationProducer;
import com.project.notification.request.NotificationRequest;
import com.project.notification.response.NotificationResponse;
import com.project.notification.response.ResponseForUser;
import com.project.notification.response.ResponseFromId;
import com.project.notification.service.NotificationService;
import com.project.notification.service.OutBoxEventServiceImpl;
import com.project.notification.transformer.NotificationTransformer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationFacade {
    private final NotificationTransformer transformer;
    private final NotificationService service;
    private final NotificationProducer producer;
    private final OutBoxEventServiceImpl outBoxEventService;

    public NotificationResponse makeNotification(NotificationRequest request) {
        try {
            Notification notification =
                    Notification.builder()
                            .userId(request.getUserId())
                            .title(request.getTitle())
                            .message(request.getMessage())
                            .type(request.getType())
                            .isRead(false)
                            .build();

            Notification savedNotification = service.makeNotification(notification);

            NotificationResponse response = null;
            if (savedNotification != null) {
                response = transformer.toNotificationResponse(notification);
            }
            else{
                throw new ServiceException(
                        "ERR123",
                        "Notification not generated",
                        "CT123",
                        "sv123",
                        HttpStatus.NOT_IMPLEMENTED
                );
            }
            return response;
        }catch (Exception ex){
            throw new ServiceException(
                    "ERR123",
                    ex.getMessage(),
                    "ct123",
                    "sv123",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public List<ResponseForUser> getNotificationUser(long userId) {
        try {
            List<Notification> notifications = service.getNotificationByUserId(userId);

            return transformer.toResponseForUser(notifications);
        }catch (Exception ex){
            throw new ServiceException(
                    "ERR456",
                    ex.getMessage(),
                    "ct456",
                    "sv456",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public ResponseFromId getNotificationId(long id) {
        try {
            Notification notifications = service.getNotificationById(id);
            ResponseFromId responseFromId = ResponseFromId.builder()
                    .id(notifications.getId())
                    .userId(notifications.getUserId())
                    .title(notifications.getTitle())
                    .message(notifications.getMessage())
                    .type(notifications.getType())
                    .build();
            return responseFromId;
        }catch (Exception ex){
            throw new ServiceException(
                    "ERR789",
                    ex.getMessage(),
                    "ct789",
                    "sv789",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void markAsRead(long id) {
        try {
            Notification notification =
                    service.getNotificationById(id);

            notification.setIsRead(true);

            service.save(notification);
        }catch (Exception ex){
            throw new ServiceException(
                    "ERR90",
                    ex.getMessage(),
                    "ct90",
                    "sv90",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
    @Transactional
    public void publishNotification(NotificationRequest request) {
        NotificationEvent event=NotificationEvent.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .isRead(false)
                .build();
        outBoxEventService.saveNotificationEvent(event);
        log.info("publishNotification event published"+event);
    }
}
