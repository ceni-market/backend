package com.cenimarket.backend.transaction.dto.response;

import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.transaction.domain.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionCompleteResponse {
    @NotNull
    @Positive
    private Long transactionId;
    @NotNull
    private TransactionStatus status;
    @NotNull
    private LocalDateTime completedAt;
    @NotNull
    private ListingStatus listingStatus;
}
