package com.cenimarket.backend.user.service;

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
public class ProfileService {

    private final UserRepository userRepository;
    // private final S3Service s3Service; // 실제 구현 시 S3 사용 권장

    @Transactional
    public String updateProfileImage(String email, MultipartFile file) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 2. 파일 저장 (예: S3에 업로드 후 URL 반환받음)
        // String imageUrl = s3Service.upload(file);
        String imageUrl = "/uploads/profiles/" + file.getOriginalFilename(); // 임시 로컬 경로 예시

        // 3. 유저 엔티티 업데이트
        user.updateProfileImage(imageUrl);

        return imageUrl;
    }
}