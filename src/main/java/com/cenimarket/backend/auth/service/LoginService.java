package com.cenimarket.backend.auth.service;


import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import com.cenimarket.backend.auth.repository.EmailVerificationRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.domain.UserStatus;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 암호화용
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cenimarket.backend.global.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //보안 알고리즘의 유연한 변경,관심사 분리,테스트 용이성
    private final JwtTokenProvider jwtTokenProvider; // JWT 발급을 위한 컴포넌트
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인 비즈니스 로직
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. 이메일 존재 여부 확인 (유저상태도 확인함)

        User user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }


        // 3. 토큰 생성 (AccessToken, RefreshToken)
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        // JwtTokenProvider에서 설정된 만료 시간을 가져오거나 상수로 정의된 값을 넘깁니다.
        long expiresIn = jwtTokenProvider.getAccessTokenExpiration();

        refreshTokenService.saveOrUpdate(user, refreshToken);

        // 4. 응답 DTO 반환
        return LoginResponseDTO.of(accessToken, refreshToken,expiresIn);
    }
}