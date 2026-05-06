package com.cenimarket.backend.transaction.dto.response;

import com.cenimarket.backend.transaction.domain.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionDetailResponse {
    @NotNull
    @Positive
    private Long transactionId;
    @NotNull
    private TransactionStatus status;
    @NotNull
    private LocalDateTime completedAt;

    private Long chatRoomId;

    @Builder
    public TransactionDetailResponse(Long transactionId, TransactionStatus status, LocalDateTime completedAt, Long chatRoomId){
        this.transactionId = transactionId;
        this.status = status;
        this.completedAt = completedAt;
        this.chatRoomId = chatRoomId;
    }

    public static class ListingInfo {
        private Long listingId;
        private String title;
        private Integer price;
    }

    public static class UserInfo {

    }
    private Long seller;
    private Long buyer;

    //좀 더 고민해봐야 할 듯
}
