package com.cenimarket.backend.transaction.dto.request;

import com.cenimarket.backend.transaction.domain.TransactionStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionStatusRequest {
    private TransactionStatus status;
}
