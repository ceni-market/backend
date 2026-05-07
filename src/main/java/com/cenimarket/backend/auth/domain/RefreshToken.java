package com.cenimarket.backend.auth.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // 이 코드는 "이 리프레시 토큰은 반드시 특정 한 명의 유저와만 연결되어야 하며,
    // 성능 최적화를 위해 유저 정보는 필요할 때만 불러온다
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    /**
     * 새로운 리프레시 토큰 생성을 위한 생성자
     */
    public RefreshToken(String token, User user, long expirationMillis) {
        this.token = token;
        this.user = user;
        this.expireDate = LocalDateTime.now().plusNanos(expirationMillis * 1_000_000);
    }

    /**
     * 기존 토큰 갱신 (Rotation 정책 적용 시 사용)
     */
    public void updateToken(String newToken, long expirationMillis) {
        this.token = newToken;
        this.expireDate = LocalDateTime.now().plusNanos(expirationMillis * 1_000_000);
    }
}