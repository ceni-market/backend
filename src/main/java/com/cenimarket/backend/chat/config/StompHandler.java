package com.cenimarket.backend.chat.config;

import com.cenimarket.backend.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("WebSocket통신 connect요청 들어옴. 토큰 유효성 검사 진행.");
            //요청의 헤더에서 Authorization값 가져옴.
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = "";
            System.out.println(bearerToken);
            if(bearerToken != null){
                //bearerToken에서 prefix제거. "Bearer " <- 이거 떼고 뒤에 있는 accessToken만 가져온다.
                token = bearerToken.substring(7);
            } else {
                token = (String) accessor.getSessionAttributes().get("USER_TOKEN");
            }
            System.out.println(token);
            //토큰 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료!");

            String userEmail = claims.getSubject();

            //Userprincipal 객체 생성해서 StompHeaderAccessor에 보관.
            if(userEmail != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                // 스프링 시큐리티 인증 객체 반환
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                accessor.setUser(authentication);
            }
        }
        return message;
    }
}
