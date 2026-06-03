package com.cenimarket.backend.notification.dto.response;

import com.cenimarket.backend.chat.domain.MessageType;
import com.cenimarket.backend.notification.domain.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatNotificationResponse {

    private Long notiId;
    private NotificationType notificationType;
    private boolean isRead;
    private LocalDateTime createdAt;

    private Long chatRoomId;
    private String messagePreview;
    private MessageType messageType;

    @Builder
    private ChatNotificationResponse(Long notiId, NotificationType notificationType, boolean isRead, LocalDateTime createdAt,
                                     Long chatRoomId, String messagePreview, MessageType messageType) {
        this.notiId = notiId;
        this.notificationType = notificationType;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.chatRoomId = chatRoomId;
        this.messagePreview = messagePreview;
        this.messageType = messageType;
    }

    public static ChatNotificationResponse from(ChatNotification cn) {
        return ChatNotificationResponse.builder()
                .notiId(cn.getId())
                .notificationType(cn.getNotiType())
                .isRead(cn.isRead())
                .createdAt(cn.getCreatedAt())
                .chatRoomId(cn.getChatRoom().getId())
                .messagePreview(cn.getMessagePreview())
                .messageType(cn.getMessageType())
                .build();
    }
}