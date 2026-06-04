package com.cenimarket.backend.notification.domain;

import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.domain.MessageType;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.apache.tomcat.util.buf.MessageBytes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("CHAT")
public class ChatNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, length = 255)
    private String messagePreview;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Builder
    public ChatNotification(User receiver, NotificationType notiType, ChatRoom chatRoom, User sender, String messagePreview, MessageType messageType) {
        super(receiver, notiType);
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.messagePreview = messagePreview;
        this.messageType = messageType;
    }

    public static ChatNotification from(ChatRoom chatRoom, NotificationType notiType, User sender, String messagePreview, MessageType messageType) {
        return ChatNotification.builder()
                .receiver(chatRoom.getTargetUser(sender.getId()))
                .notiType(notiType)
                .chatRoom(chatRoom)
                .sender(sender)
                .messagePreview(messagePreview)
                .messageType(messageType)
                .build();
    }
}