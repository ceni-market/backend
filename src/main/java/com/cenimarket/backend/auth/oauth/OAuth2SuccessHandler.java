package com.cenimarket.backend.auth.oauth;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.security.JwtTokenProvider;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider; // JWT 발급 클래스
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        System.out.println("여긴되나");
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        user.setLastLoginAt(LocalDateTime.now()); // 시간을 현재로 세팅
        userRepository.save(user); // DB에 반영

        // 1. JWT 토큰 생성 (기존 토큰 생성 로직 활용)
        String accessToken = jwtTokenProvider.createAccessToken(userPrincipal.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(userPrincipal.getEmail());

        // 2. 프론트엔드 리다이렉트 주소 설정
        // 수동 가입 시 입력했던 이름, 메일 등의 정보를 쿼리 파라미터로 보낼 수도 있습니다.
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
