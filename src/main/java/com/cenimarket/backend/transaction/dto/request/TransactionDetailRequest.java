package com.cenimarket.backend.transaction.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionDetailRequest {
    @NotNull
    @Positive
    private Long transactionId;

    @Builder
    public TransactionDetailRequest(Long transactionId){
        this.transactionId = transactionId;
    }
}
