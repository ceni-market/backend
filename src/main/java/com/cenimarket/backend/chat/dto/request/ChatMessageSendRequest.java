package com.cenimarket.backend.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageSendRequest { //메시지 발신용 DTO. 보낼 메시지의 내용과 누가 보냈는지 Email 같이 보낸다.
    @NotBlank
    private String message;
    @NotNull
    private String senderEmail;
}
