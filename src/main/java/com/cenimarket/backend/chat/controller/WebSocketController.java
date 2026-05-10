package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.chat.dto.request.ChatMessageSendRequest;
import com.cenimarket.backend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebSocketController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messageSendingOperations;

    public WebSocketController(ChatService chatService, SimpMessageSendingOperations messageSendingOperations) {
        this.chatService = chatService;
        this.messageSendingOperations = messageSendingOperations;
    }


    /* config(WebSocketConfig.java)에 설정한 바와 같이 /publish 주소로 오는 요청은 여기로 라우팅된다.
    @DestinationVariable은 @MessageMapping과 함께 사용된다.
    @MessageMapping으로 전달되는 데이터의 목적지 주소를 가져온다. */
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageSendRequest messageSendRequest) {
        chatService.saveMessage(roomId, messageSendRequest);
        //@SendTo 어노테이션으로도 할 수 있으나, 어노테이션을 이용하면 코드의 유연성이 떨어지기 때문에 따로 구현.
        messageSendingOperations.convertAndSend("/topic/" + roomId, messageSendRequest);
    }
}
