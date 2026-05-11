package com.cenimarket.backend.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    public WebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOriginPatterns("*")
                .withSockJS(); //프론트엔드 기술. ws: 대신에 http: 엔드포인트를 사용할 수 있게 해준다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /publish/{채팅방ID} 형태의 주소에 메시지를 발행해야함을 설정.
        // /publish로 시작하는 url패턴으로 메시지가 발행되면 @Controller객체의 @MessageMapping 메서드로 라우팅 된다.
        registry.setApplicationDestinationPrefixes("/publish");
        // /topic/{채팅방ID} 형태의 주소로 메시지를 구독(받아볼 수 있음)할 수 있음을 설정.
        registry.enableSimpleBroker("/topic");
    }
//  jwt토큰 검증을 위한 핸들러 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
