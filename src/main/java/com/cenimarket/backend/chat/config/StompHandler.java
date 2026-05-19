package com.cenimarket.backend.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("WebSocket통신 connect요청 들어옴. 토큰 유효성 검사 진행.");
            //요청의 헤더에서 Authorization값 가져옴.
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            //bearerToken에서 prefix제거. "Bearer " <- 이거 떼고 뒤에 있는 순수 accessToken만 가져온다.
            String token = bearerToken.substring(7);
            System.out.println(token);
            //토큰 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료!");

            String userEmail = claims.getSubject();

            if(userEmail != null) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        Collections.emptyList()
                );

                accessor.setUser(authentication);

                accessor.getSessionAttributes().put("userEmail", userEmail);
            }
        }
        return message;
    }
}
