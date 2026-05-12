package com.cenimarket.backend.user.domain;

import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends SoftDeleteEntity { // BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // 회원 ID (BIGINT, PK)

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email; // 이메일 (VARCHAR(255), UK)

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash; // 암호화 비밀번호 (VARCHAR(255))

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 이름 (VARCHAR(50))

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE; // 회원 상태 (기본값 ACTIVE)

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl; // 프로필 이미지 (VARCHAR(500))

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt; // 이메일 인증일시

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // 마지막 로그인일시

    public static User createNewUser(String name, String email, String encodedPassword) {
        return User.builder()
                .name(name)
                .email(email)
                .passwordHash(encodedPassword)
                .status(UserStatus.ACTIVE) // 기본값 명시적 설정
                .build();
    }

    public void updatePassword(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
    }

    public void setEmailVerifiedAt(LocalDateTime now) {
        this.emailVerifiedAt=now;
    }

    public void setLastLoginAt(LocalDateTime now) {
        this.lastLoginAt=now;
    }
}

