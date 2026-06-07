package com.cenimarket.backend.chat.config;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

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

//        Long roomId = Long.valueOf(destination.substring(destination.lastIndexOf("/") + 1));

        System.out.println("구독!!" + destination);

//        accessor.getSessionAttributes().put("roomId", roomId);
    }

    @EventListener
    public void unSubscribeHandler(SessionUnsubscribeEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();

        System.out.println("구독 해제!!" + destination);
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
    }
}
