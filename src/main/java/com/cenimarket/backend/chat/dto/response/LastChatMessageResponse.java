package com.cenimarket.backend.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class LastChatMessageResponse {
    private Long id;
    private LocalDateTime createdAt;

    @Builder
    public LastChatMessageResponse(Long id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static LastChatMessageResponse of(Long id, LocalDateTime createdAt){
        return LastChatMessageResponse.builder()
                .id(id)
                .createdAt(createdAt)
                .build();
    }
}
