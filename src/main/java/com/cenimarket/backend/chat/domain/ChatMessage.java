package com.cenimarket.backend.chat.domain;

import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(length = 500, columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Builder
    public ChatMessage(Long id, MessageType messageType, String content, User user, ChatRoom chatRoom){
        this.id = id;
        this.messageType = messageType;
        this.content = content;
        this.sender = user;
        this.chatRoom = chatRoom;
    }
}

