package com.cenimarket.backend.user.service;

import com.cenimarket.backend.auth.repository.RefreshTokenRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.dto.request.PasswordUpdateRequest;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PasswordUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void updatePassword(String email, PasswordUpdateRequest request) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 2. 새 비밀번호와 확인용 비밀번호 일치 여부 검증 (추가된 로직)
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 3. 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 4. 새 비밀번호 암호화 및 저장
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encryptedPassword);

        // 5. 보안을 위해 기존 리프레시 토큰 삭제 (선택 사항)
        refreshTokenRepository.deleteByUser(user);
    }
}