package com.cenimarket.backend.chat.dto;

import com.cenimarket.backend.chat.domain.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static java.awt.SystemColor.text;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto { //메시지 전송용 수신 발신 공용 DTO. 보낼 메시지의 내용과 누가 보냈는지 Email 같이 보낸다.
    @NotBlank
    private String message;
    @NotNull
    private String senderEmail;
    @NotNull
    private MessageType contentType;

    private LocalDateTime createdAt;

    @Builder
    public ChatMessageDto(String message, String senderEmail, MessageType contentType, LocalDateTime createdAt){
        this.message = message;
        this.senderEmail = senderEmail;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }

    public static ChatMessageDto from (String message, String senderEmail, MessageType contentType, LocalDateTime createdAt ) {
        return ChatMessageDto.builder()
                .message(message)
                .senderEmail(senderEmail)
                .contentType(contentType)
                .createdAt(createdAt)
                .build();
    }
}
