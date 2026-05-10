package com.cenimarket.backend.mypage.dto.response;

import com.cenimarket.backend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private Long id;                // 고유 식별값 (내 게시글 찾기 등에 필요)
    private String email;           // 사용자 계정 정보
    private String name;            // 화면에 표시될 이름
    private String profileImageUrl; // 프로필 이미지 (없으면 기본 이미지 처리용)
    private String status;          // 현재 계정 상태 (ACTIVE 등)

    // 추가정보
    private boolean isEmailVerified; // 이메일 인증 여부 (인증 안 된 유저 알림용)

    // 엔티티를 DTO로 변환하는 생성 메서드
    public static MemberResponseDTO of(User user) {
        return MemberResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .status(user.getStatus().name())
                .isEmailVerified(user.getEmailVerifiedAt() != null)
                .build();
    }
}