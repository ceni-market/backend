package com.cenimarket.backend.user.service;

import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.upload.dto.ImageUploadResponse;
import com.cenimarket.backend.upload.service.ImageUploadService;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public String updateProfileImage(String email, MultipartFile file) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        //파일이 비어있는지 검증
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 단일 파일을 리스트(List.of)로 감싸서 호출
        ImageUploadResponse response = imageUploadService.uploadImages(List.of(file));
        // 2. 파일 저장 (예: S3에 업로드 후 URL 반환받음)
        String imageUrl = response.getImageUrls().get(0);
        //String imageUrl = "/uploads/profiles/" + file.getOriginalFilename(); // 임시 로컬 경로 예시

        // 3. 유저 엔티티 업데이트
        user.updateProfileImage(imageUrl);

        return imageUrl;
    }
}