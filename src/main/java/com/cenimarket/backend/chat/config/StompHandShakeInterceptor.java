package com.cenimarket.backend.chat.config;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class StompHandShakeInterceptor implements HandshakeInterceptor {

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        // 요청에 쿠키가 하나라도 실려 있다면 전수 조사를 시작
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // 내가 찾고 있는 쿠키 이름("accessToken" 또는 "refreshToken")과 일치하면 그 값을 반환
                if (cookieName.equals(cookie.getName())) return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            Cookie[] cookies = httpRequest.getCookies();

            if(cookies != null){
                for(Cookie cookie : cookies) {
                    if("accessToken".equals(cookie.getName())) {
                        String tokenValue = cookie.getValue();
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        System.out.println("여기는 핸드셰이크 인터셉터" + tokenValue);
                        attributes.put("USER_TOKEN", tokenValue);
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {}

}
