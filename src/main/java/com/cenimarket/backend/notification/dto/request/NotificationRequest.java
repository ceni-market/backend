package com.cenimarket.backend.notification.dto.request;

import com.cenimarket.backend.notification.domain.Notification;
import com.cenimarket.backend.notification.domain.NotificationType;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationRequest {
    private String content;
    private NotificationType type;

    @Builder
    public NotificationRequest (String content, NotificationType type) {
        this.content  = content;
        this.type = type;
    }

    public static NotificationRequest of(Notification notification){
        return NotificationRequest.builder()
                .content(notification.getContent())
                .type(notification.getType())
                .build();
    }
}