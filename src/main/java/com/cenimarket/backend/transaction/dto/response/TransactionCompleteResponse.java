package com.cenimarket.backend.transaction.dto.response;

import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.transaction.domain.Transaction;
import com.cenimarket.backend.transaction.domain.TransactionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionCompleteResponse {

    private Long transactionId;
    private TransactionStatus status;
    private LocalDateTime completedAt;
    private ListingStatus listingStatus;

    public static TransactionCompleteResponse from(Transaction transaction) {
        return new TransactionCompleteResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getCompletedAt(),
                transaction.getListing().getStatus()
        );
    }
}
