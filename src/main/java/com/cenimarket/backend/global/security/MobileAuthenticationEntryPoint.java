package com.cenimarket.backend.global.security;

import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MobileAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI=request.getRequestURI();

        // 1. 모바일 페이지 보안 구역(/mobile/...)에 인증 없이 접근한 경우
        if(requestURI.startsWith("/mobile")){
            if (requestURI.equals("/mobile/login")) {
                return;
            }
            // 미인증 유저는 로그인 화면으로 자동 리다이렉트
            response.sendRedirect("/mobile/login");
        }
    }
}
