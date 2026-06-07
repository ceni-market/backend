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
import java.time.LocalDateTime;

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
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto messageSendRequest) {
        chatService.saveMessage(roomId, messageSendRequest);

        chatService.updateReadAt(roomId, messageSendRequest.getSenderEmail());

        webSocketService.createChatNoti(roomId, messageSendRequest);

        ChatMessageDto message = ChatMessageDto.insertCreatedAt(messageSendRequest);
        messageSendingOperations.convertAndSend("/queue/chat/" + roomId, message);
    }
}
