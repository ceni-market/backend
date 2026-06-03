package com.cenimarket.backend.notification.dto.request;

import com.cenimarket.backend.notification.domain.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationRequest {

    private String type;
    private String content;

    @Builder
    private NotificationRequest(String type, String content) {
        this.content = content;
        this.type = type;
    }
}