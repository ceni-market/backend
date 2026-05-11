package com.cenimarket.backend.transaction.domain;

import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.global.domain.BaseEntity;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false, unique = true)
    private ChatRoom chatRoom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false, unique = true)
    private Listing listing;

    @Builder
    public Transaction(Integer price,
                       LocalDateTime completedAt,
                       TransactionStatus status,
                       TransactionType type,
                       User seller,
                       User buyer,
                       ChatRoom chatRoom,
                       Listing listing) {
        this.price = price;
        this.completedAt = completedAt;
        this.status = status;
        this.type = type;
        this.seller = seller;
        this.buyer = buyer;
        this.chatRoom = chatRoom;
        this.listing = listing;
    }

    public static Transaction createCompleted(
            Listing listing,
            User seller,
            User buyer,
            ChatRoom chatRoom,
            Integer price,
            TransactionType transactionType,
            LocalDateTime completedAt
    ){
        return Transaction.builder()
                .listing(listing)
                .seller(seller)
                .buyer(buyer)
                .chatRoom(chatRoom)
                .price(price)
                .type(transactionType)
                .status(TransactionStatus.COMPLETED)
                .completedAt(completedAt)
                .build();
    }

}
