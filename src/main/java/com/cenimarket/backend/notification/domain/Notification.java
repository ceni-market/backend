package com.cenimarket.backend.notification.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isRead = false;

    @Builder
    private Notification(User receiver, NotificationType type, Long targetId, String content) {
        this.receiver = receiver;
        this.type = type;
        this.targetId = targetId;
        this.content = content;
    }

    public void read() {
        this.isRead = true;
    }
}