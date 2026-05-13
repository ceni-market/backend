package com.cenimarket.backend.chat.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomListResponse { //채팅방 목록 조회
    @NotNull
    private Long chatRoomId;

    @Builder
    ChatRoomListResponse (Long chatRoomId){
        this.chatRoomId = chatRoomId;
    }
}
