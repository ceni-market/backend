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
    private ChatMessageListRequest firstMessage;

    @Builder
    public ChatRoomCreateRequest(Long listingId, ChatMessageListRequest firstMessage) {
        this.listingId = listingId;
        this.firstMessage = firstMessage;
    }
}