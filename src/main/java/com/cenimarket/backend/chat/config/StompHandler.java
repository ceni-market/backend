package com.cenimarket.backend.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("WebSocket통신 connect요청 들어옴. 토큰 유효성 검사 진행.");

            // 1. 헤더에서 Authorization 값 1순위 조회
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            System.out.println("기존 헤더 토큰: " + bearerToken);

            // 2. 헤더에 없다면 핸드셰이크 인터셉터가 담아준 세션 가방에서 2순위 조회
            if (bearerToken == null) {
                // accessor.getSessionAttributes() 대신 message 헤더 안의 세션 맵을 안전하게 꺼냅니다.
                Map<String, Object> sessionAttributes = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");

                if (sessionAttributes != null) {
                    String userToken = (String) sessionAttributes.get("USER_TOKEN");
                    if (userToken != null) {
                        // 쿠키 토큰에 "Bearer " 접두사가 없다면 붙여줍니다. (substring(7) 방어용)
                        bearerToken = userToken.startsWith("Bearer ") ? userToken : "Bearer " + userToken;
                    }
                }
            }

            System.out.println("최종 검증에 사용할 토큰: " + bearerToken);
//        if(StompCommand.CONNECT == accessor.getCommand()){
//            System.out.println("WebSocket통신 connect요청 들어옴. 토큰 유효성 검사 진행.");
//            //요청의 헤더에서 Authorization값 가져옴.
//            String bearerToken = accessor.getFirstNativeHeader("Authorization");
//            System.out.println("ldaksfj"+ bearerToken);
//            if(bearerToken == null){
//                bearerToken = (String) accessor.getSessionAttributes().get("USER_TOKEN");
//            }
//            System.out.println("ldaksfj123"+ bearerToken);
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
            //Userprincipal 객체 생성.
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
