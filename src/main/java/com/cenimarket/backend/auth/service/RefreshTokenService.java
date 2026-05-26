package com.cenimarket.backend.auth.service;


import com.cenimarket.backend.auth.domain.RefreshToken;
import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.request.RefreshTokenRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.dto.response.RefreshTokenResponseDTO;
import com.cenimarket.backend.auth.repository.RefreshTokenRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.security.JwtTokenProvider;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 암호화용
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenR;
    private final JwtTokenProvider jwtTokenProvider; // JWT 발급을 위한 컴포넌트
    private final UserRepository userRepository;

    /**
     * [재발급] 컨트롤러에서 호출하는 비즈니스 로직
     */
    @Transactional
    public RefreshTokenResponseDTO reissue(String oldRefreshToken) {
        // 1. 검증 및 유저 확보
        RefreshToken savedToken = findAndValidate(oldRefreshToken);
        User user = savedToken.getUser();

        // 2. 새로운 토큰 쌍 생성
        String newAt = jwtTokenProvider.createAccessToken(user.getEmail());
        String newRt = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 3. DB 정보 갱신 (아래의 내부 메서드 활용)
        saveOrUpdate(user, newRt);

        return RefreshTokenResponseDTO.of(newAt, newRt, jwtTokenProvider.getAccessTokenExpiration());
    }


    /**
     * 1. 토큰 저장 및 갱신 (로그인 시 호출)
     * 기존 토큰이 있다면 덮어쓰고, 없으면 새로 생성합니다.
     */
    @Transactional
    public void saveOrUpdate(User user, String tokenValue) {
        long expirationMillis = jwtTokenProvider.getRefreshTokenExpiration();

        // 1:1 관계를 유지하기 위해 기존 토큰 유무 확인

        // Optional<RefreshToken>: DB 조회 결과가 "있을 수도 있고 없을 수도 있음"을 나타냄
        Optional<RefreshToken> optionalToken = refreshTokenR.findByUser(user);

        if (optionalToken.isPresent()) {
            // 2. [기존 토큰이 있는 경우] : 값을 새것으로 업데이트합니다.
            RefreshToken existingToken = optionalToken.get();
            existingToken.updateToken(tokenValue, expirationMillis);
            // @Transactional 덕분에 별도로 save를 호출하지 않아도 DB에 반영(Dirty Checking)됩니다.
        } else {
            // 3. [기존 토큰이 없는 경우] : 새로 만들어서 저장합니다.
            RefreshToken newToken = new RefreshToken(tokenValue, user, expirationMillis);
            refreshTokenR.save(newToken);
        }
    }

    /**
     * 해당 클래스 전용: 토큰 유효성 검증 및 일치 여부 확인 후 유효한 토큰 객체 반환
     */
    private RefreshToken findAndValidate(String tokenValue) {
        // 1. DB에서 토큰 값으로 데이터를 찾습니다.
        Optional<RefreshToken> optionalToken = refreshTokenR.findByToken(tokenValue);

        // 2. 만약 DB에 토큰이 아예 없다면 예외를 발생시킵니다.
        if (optionalToken.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 3. 토큰이 존재한다면 객체를 꺼냅니다.
        RefreshToken savedToken = optionalToken.get();

        // 4. 꺼낸 토큰의 만료 시간(expireDate)이 현재 시간보다 이전인지 체크합니다.
        if (savedToken.getExpireDate().isBefore(LocalDateTime.now())) {

            // 예외를 던져서 로그아웃 처리를 유도합니다.
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 5. 모든 검증을 통과했다면 유효한 토큰 객체를 반환합니다.
        return savedToken;
    }

    /**
     * 모바일 타임리프 필터 전용: 토큰 유효성 검증 및 일치 여부 확인
     * 토큰이 DB에 존재하고 만료되지 않았다면 true, 문제 있다면 false를 반환
     */
    public boolean isValidRefreshToken(String tokenValue) {
        try {
            // 1. private 검증 메서드를 그대로 활용
            RefreshToken savedToken = findAndValidate(tokenValue);

            // 2. 예외 없이 정상적으로 토큰 객체가 반환되었다면 유효한 토큰
            return savedToken != null;
        } catch (Exception e) {
            // 3. 만약 findAndValidate 안에서 BusinessException(만료, 토큰없음)이 터지면 false를 리턴
            return false;
        }
    }

    /**
     * 📌 로그아웃 전용: 이메일 기반 리프레시 토큰 삭제
     * - 만약 사용자가 연속으로 로그아웃을 누르거나 이미 토큰이 유실된 상태여도
     * 에러가 터지지 않도록 .ifPresent()로 안전하게 방어 제어합니다.
     */
    @Transactional // 쓰기 권한 부여
    public void deleteByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenR.findByUser(user).ifPresent(token -> {
                refreshTokenR.delete(token);
            });
        });
    }
}