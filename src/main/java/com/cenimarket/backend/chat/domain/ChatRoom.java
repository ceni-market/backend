package com.cenimarket.backend.chat.domain;

import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.notification.domain.ChatNotification;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "last_message_id")
    private ChatMessage lastMessage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatNotification> notifications;

    @Builder
    public ChatRoom(LocalDateTime lastMessageAt, User seller, User buyer, Listing listing, ChatMessage lastMessage) {
        this.lastMessageAt = lastMessageAt;
        this.seller = seller;
        this.buyer = buyer;
        this.listing = listing;
        this.lastMessage = lastMessage;
    }

    public static ChatRoom from(User buyer, User seller, Listing listing){
        return ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .listing(listing)
                .build();
    }

    public User getTargetUser(Long userId) {
        if(userId.equals(seller.getId())){
            return buyer;
        } else {
            return seller;
        }
    }

    public void updateLastMessage(ChatMessage message) {
        this.lastMessage = message;
        this.lastMessageAt = message.getCreatedAt();
    }

    public void updateListing(Listing listing){
        this.listing = listing;
    }
}
