package com.cenimarket.backend.transaction.dto.response;

import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.transaction.domain.TransactionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionStatusResponse {
    private Long transactionId;
    private TransactionStatus status;
    private LocalDateTime completedAt;
    private ListingStatus listingStatus;

    @Builder
    public TransactionStatusResponse(Long transactionId, TransactionStatus status, LocalDateTime completedAt, ListingStatus listingStatus){
        this.transactionId = transactionId;
        this.status = status;
        this.completedAt = completedAt;
        this.listingStatus = listingStatus;
    }
}
