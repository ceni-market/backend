package com.cenimarket.backend.chat.domain;

import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_rooms")
public class ChatRoom extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastMessageAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private ChatMessage lastMessage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Builder
    public ChatRoom(Long id, LocalDateTime lastMessageAt, User seller, User buyer, Listing listing, ChatMessage lastMessage) {
        this.id = id;
        this.lastMessageAt = lastMessageAt;
        this.seller = seller;
        this.buyer = buyer;
        this.listing = listing;
        this.lastMessage = lastMessage;
    }

    public User getTargetUser(Long userId) {
        if(userId.equals(seller.getId())){
            return buyer;
        } else {
            return seller;
        }
    }

    public void updateLastMessage(ChatMessage message, LocalDateTime lastMessageAt) {
        this.lastMessage = message;
        this.lastMessageAt = lastMessageAt;
    }
}
