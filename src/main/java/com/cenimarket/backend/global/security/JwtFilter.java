package com.cenimarket.backend.global.security;

import com.cenimarket.backend.global.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // 소셜 로그인 관련 경로는 토큰 검사를 하지 않고 통과시킨다.
        if (path.startsWith("/login/oauth2/") || path.startsWith("/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 1. 요청 헤더에서 토큰을 꺼내옴
        String jwt = resolveToken(request);

        // 2. 토큰이 있고, 판독기(provider)로 검사했을 때 정상이라면?
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 3. 신분증(Authentication)을 만들어서 서버 주머니에 넣음
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 4. 다음 단계(필터)로 넘김
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer " 뒤의 토큰값만 쏙 빼오는 메서드
    private String resolveToken(HttpServletRequest request) {

        // 1. 헤더 검사 (REST API)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 쿠키 검사 (타임리프 SSR 모바일 환경)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}