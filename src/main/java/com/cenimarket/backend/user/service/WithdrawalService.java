package com.cenimarket.backend.user.service;

import com.cenimarket.backend.auth.repository.RefreshTokenRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.domain.UserStatus;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(String email, String password) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 💡 [디펜시브 추가] 이미 탈퇴 처리된 유저가 또 탈퇴 요청을 보낸 경우 방어
        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }


        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 3. 유저 삭제 (또는 상태 변경)
        user.withdraw();

        // 4. 연관 데이터 처리 (리프레시 토큰 등)
        refreshTokenRepository.deleteByUser(user);
    }
}
