package com.cenimarket.backend.auth.domain;

import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "email_verifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerification extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    private VerificationPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;


    public static EmailVerification create(String email, String token, VerificationPurpose purpose) {
        return EmailVerification.builder()
                .email(email)
                .token(token)               // 필드명 'token'에 매핑
                .purpose(purpose)           // String이 아닌 열거형(Enum)으로 수신
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // 만료 정책 유지
                .verifiedAt(null)           // 생성 시점에는 인증되지 않았으므로 null
                .build();
    }
}