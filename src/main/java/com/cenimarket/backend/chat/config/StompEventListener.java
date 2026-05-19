package com.cenimarket.backend.chat.config;

import com.cenimarket.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final ChatService chatService;

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    @EventListener
    public void subscribeHandler(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();

        Long roomId = Long.valueOf(destination.substring(destination.lastIndexOf("/") + 1));

        accessor.getSessionAttributes().put("roomId", roomId);
    }

    @EventListener
    public void connectedHandler(SessionConnectedEvent event) {
        //세션 연결 로그
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        sessions.add(sessionId);
        System.out.println("connect session ID : " + sessionId);
        System.out.println("total sessions : " + sessions.size());
    }

    @EventListener
    public void disconnectHandler(SessionDisconnectEvent event) {
        //커넥션 해제한 세션 모니터링
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        sessions.remove(sessionId);
        System.out.println("disconnect session ID : " + sessionId);
        System.out.println("total sessions : " + sessions.size());

        // 채팅이 끊어지면, 채팅 멤버의 마지막 조회 시간을 수정하는 컨트롤러 메서드를 호출.
        String userEmail = (String) accessor.getSessionAttributes().get("userEmail");
        Long roomId = (Long) accessor.getSessionAttributes().get("roomId");
//        Long roomId = (Long)sessionMap.get(sessionId);
        chatService.setLastReadAt(userEmail, roomId);
    }
}
