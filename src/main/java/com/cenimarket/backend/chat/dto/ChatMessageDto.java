package com.cenimarket.backend.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto { //메시지 전송용 수신 발신 공용 DTO. 보낼 메시지의 내용과 누가 보냈는지 Email 같이 보낸다.
    @NotBlank
    private String message;
    @NotNull
    private String senderEmail;

    @Builder
    public ChatMessageDto(String message, String senderEmail){
        this.message = message;
        this.senderEmail = senderEmail;
    }
}
