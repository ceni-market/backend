package com.cenimarket.backend.chat.dto.response;

import com.cenimarket.backend.chat.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class LastChatMessageResponse {
    private Long id;
    private LocalDateTime createdAt;

    @Builder
    public LastChatMessageResponse(Long id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static LastChatMessageResponse of(ChatMessage savedMessage){
        return LastChatMessageResponse.builder()
                .id(savedMessage.getId())
                .createdAt(savedMessage.getCreatedAt())
                .build();
    }
}
