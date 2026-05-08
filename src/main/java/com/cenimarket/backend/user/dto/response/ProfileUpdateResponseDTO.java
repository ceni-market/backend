package com.cenimarket.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileUpdateResponseDTO {
    private String email;
    private String profileImageUrl;
    private String message;

    public static ProfileUpdateResponseDTO success(String email, String url) {
        return new ProfileUpdateResponseDTO(email, url, "프로필 이미지가 성공적으로 변경되었습니다.");
    }
}
