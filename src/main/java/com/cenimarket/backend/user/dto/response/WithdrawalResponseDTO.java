package com.cenimarket.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class WithdrawalResponseDTO {
    private final boolean success;
    private final String message;
    private final LocalDateTime withdrawnAt;
    private final String redirectUrl; // 탈퇴 후 이동할 경로 (예: "/")

    /**
     * 회원 탈퇴 성공 응답을 생성하는 정적 팩토리 메서드
     */
    public static WithdrawalResponseDTO success() {
        return WithdrawalResponseDTO.builder()
                .success(true)
                .message("회원 탈퇴가 정상적으로 처리되었습니다. 그동안 이용해 주셔서 감사합니다.")
                .withdrawnAt(LocalDateTime.now())
                .redirectUrl("/")
                .build();
    }
}
