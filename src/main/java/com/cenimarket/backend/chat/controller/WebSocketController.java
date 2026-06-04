package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.service.ChatService;
import com.cenimarket.backend.chat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final ChatService chatService;
    private final WebSocketService webSocketService;
    private final SimpMessageSendingOperations messageSendingOperations;

    /* config(WebSocketConfig.java)에 설정한 바와 같이 /publish 주소로 오는 요청은 여기로 라우팅된다.
    @DestinationVariable은 @MessageMapping과 함께 사용된다.
    @MessageMapping으로 전달되는 데이터의 목적지 주소를 가져온다. */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageDto messageSendRequest,
            Principal principal
    ) {
        log.info("[CHAT WS] received roomId={}, sender={}, principalType={}",
                roomId,
                messageSendRequest.getSenderEmail(),
                principal == null ? null : principal.getClass().getName());

        chatService.saveMessage(roomId, messageSendRequest);
        log.info("[CHAT WS] message saved roomId={}", roomId);

        chatService.updateReadAt(roomId, messageSendRequest.getSenderEmail());
        log.info("[CHAT WS] readAt updated roomId={}", roomId);

        webSocketService.createChatNoti(roomId, messageSendRequest);
        log.info("[CHAT WS] notification created roomId={}", roomId);

        messageSendingOperations.convertAndSend(
                "/queue/chat/" + roomId,
                messageSendRequest
        );
        log.info("[CHAT WS] message broadcast roomId={}", roomId);
    }
}
