package com.cenimarket.backend.chat.dto.response;

import com.cenimarket.backend.chat.domain.ChatRoom;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateResponse { //채팅방 생성, 조회(있는지 없는지 조회)
    @NotNull
    private Long chatRoomId;
    @NotNull
    private Long listingId;
    @NotNull
    private Long sellerId;
    @NotNull
    private Long buyerId;

    @Builder
    public ChatRoomCreateResponse(Long chatRoomId, Long listingId, Long sellerId, Long buyerId) {
        this.chatRoomId = chatRoomId;
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    public static ChatRoomCreateResponse from(ChatRoom chatRoom, Long listingId) {
        return ChatRoomCreateResponse.builder()
                .chatRoomId(chatRoom.getId())
                .listingId(listingId)
                .sellerId(chatRoom.getSeller().getId())
                .buyerId(chatRoom.getBuyer().getId())
                .build();
    }
}
