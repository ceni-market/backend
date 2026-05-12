package com.cenimarket.backend.transaction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionCompleteRequest {
    @NotNull
    private Long chatRoomId;
}
