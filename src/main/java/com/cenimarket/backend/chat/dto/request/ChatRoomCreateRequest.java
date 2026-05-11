package com.cenimarket.backend.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequest { //채팅방 생성, 조회(있는지 없는지 조회)

    @NotNull
    private Long listingId;
    @NotNull
    private Long sellerId;
    @NotNull
    private Long buyerId;
//    @NotNull
//    private ChatMessageListRequest firstMessage;

    @Builder
    public ChatRoomCreateRequest(Long listingId, Long sellerId, Long buyerId) {
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
//        this.firstMessage = firstMessage;
    }
}