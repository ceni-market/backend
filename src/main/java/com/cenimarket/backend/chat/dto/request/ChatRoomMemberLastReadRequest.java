package com.cenimarket.backend.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChatRoomMemberLastReadRequest {
    private LocalDateTime lastReadAt;

    @Builder
    public ChatRoomMemberLastReadRequest(LocalDateTime lastReadAt){
        this.lastReadAt = lastReadAt;
    }

    public static ChatRoomMemberLastReadRequest from(LocalDateTime lastReadAt){
        return ChatRoomMemberLastReadRequest.builder()
                .lastReadAt(lastReadAt)
                .build();
    }
}
