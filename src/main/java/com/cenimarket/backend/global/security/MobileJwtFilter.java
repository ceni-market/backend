package com.cenimarket.backend.global.security;

// 자바 & 서블릿 관련 임포트
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// 스프링 및 시큐리티 관련 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

// 프로젝트 내 커스텀 서비스 임포트
import com.cenimarket.backend.auth.service.RefreshTokenService;

@RequiredArgsConstructor
public class MobileJwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 브라우저가 보낸 쿠키 더미에서 'accessToken' 추출
        String jwt=getCookieValue(request,"accessToken");

        // 2. AccessToken이 존재하고, 유효기간도 안 지나고 멀쩡한 상태일 때 신분증을 바로 넣어 인증 통과
        if(StringUtils.hasText(jwt)&&tokenProvider.validateToken(jwt)){
            setAuth(jwt);
        }
        // 3. AccessToken이 없거나 유효기간이 만료되어 사용할 수 없는 상태일때 (재발급 시나리오)
        else{

            // 브라우저 쿠키 더미에서 수명이 긴 refreshToken 추출
            String refreshToken=getCookieValue(request, "refreshToken");

            // 2. RefreshToken이 존재하고, JWT 규격상 정상이라면?
            if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {

                // 📌 3. [새로 만든 메서드 호출] DB에 존재하고 만료도 안 되었는지 최종 검증
                if (refreshTokenService.isValidRefreshToken(refreshToken)) {

                    // 검증 완료되었으므로 유저 이메일 추출
                    String email = tokenProvider.getEmail(refreshToken);

                    // 🔥 새 AccessToken 즉석 재발급
                    String newAccessToken = tokenProvider.createAccessToken(email);

                    // 타임리프는 응답 쿠키로 갱신!
                    Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
                    newAccessCookie.setHttpOnly(true); // 자바스크립트 가로채기 방지 (보안)
                    newAccessCookie.setPath("/"); // // 전체 경로에서 쿠키 접근 가능하도록 설정
                    //newAccessCookie.setMaxAge(60 * 60 * 24); // 24시간, 개발 과정에서만 편의 위해 사용
                    newAccessCookie.setMaxAge(60 * 60 ); //1시간 추후 사용 예정
                    response.addCookie(newAccessCookie); // 응답 객체에 쿠키 적재

                    // 새로 발급받은 토큰으로 시큐리티 주머니에 신분증을 채워 넣어 정상 처리
                    setAuth(newAccessToken);
                }
            }
        }

        // 4. 인증 처리가 모두 끝났으니, 필터 또는 진짜 목적지(컨트롤러)로 요청을 배달
        filterChain.doFilter(request, response);
    }

    /**
     * 시큐리티 주머니(SecurityContextHolder)에 인증 신분증을 꽂아주는 헬퍼 메서드
     */
    private void setAuth(String token) {

        // 토큰을 판독하여 시큐리티 전용 인증 객체 생성
        Authentication auth = tokenProvider.getAuthentication(token);
        // 서버가 이 요청을 인증된 사용자로 인식하도록 컨텍스트 주머니에 신분증을 저장
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     *  브라우저 요청(Request)의 쿠키들 중 내가 원하는 이름의 쿠키 값만 찾아주는 헬퍼 메서드
     */
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
}
