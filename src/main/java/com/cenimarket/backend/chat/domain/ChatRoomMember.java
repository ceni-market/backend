package com.cenimarket.backend.chat.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_room_members")
public class ChatRoomMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastReadAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id", nullable = false)
    private ChatMessage chatMessage;

    @Builder
    public ChatRoomMember(Long id, LocalDateTime lastReadAt) {
        this.id = id;
        this.lastReadAt = lastReadAt;
    }
}
