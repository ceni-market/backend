package com.cenimarket.backend.transaction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionCompleteRequest {
    @NotNull
    private Long listingId;
    @NotNull
    private Long buyerId;

    private Optional<Long> chatRoomId;

    @Builder
    public TransactionCompleteRequest(Long listingId, Long buyerId, Optional<Long> chatRoomId){
        this.listingId = listingId;
        this.buyerId = buyerId;
        this.chatRoomId = chatRoomId;
    }
}
