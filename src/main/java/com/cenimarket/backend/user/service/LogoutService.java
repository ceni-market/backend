package com.cenimarket.backend.user.service;

import com.cenimarket.backend.auth.repository.RefreshTokenRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    // private final S3Service s3Service; // 실제 구현 시 S3 사용 권장

    @Transactional
    public void logout(String email) {
        // 유저를 찾아서 해당 유저의 refreshToken 필드를 null로 만듭니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        refreshTokenRepository.deleteByUser(user);
        // Dirty Checking에 의해 트랜잭션 종료 시 DB에 반영됩니다.
    }
}
