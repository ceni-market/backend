package com.cenimarket.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PasswordUpdateResponseDTO {
    private boolean success;
    private String message;
    private LocalDateTime updatedAt;
    private String actionRequired; // 예: "RE_LOGIN"

    /**
     * 비밀번호 변경 성공 응답을 생성하는 정적 팩토리 메서드
     */
    public static PasswordUpdateResponseDTO success() {
        return PasswordUpdateResponseDTO.builder()
                .success(true)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .updatedAt(LocalDateTime.now())
                .actionRequired("RE_LOGIN")
                .build();
    }
}