package com.cenimarket.backend.auth.repository;

import com.cenimarket.backend.auth.domain.RefreshToken;
import com.cenimarket.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 문자열로 리프레시 토큰 정보를 조회합니다.
     * 재발급 요청 시 클라이언트가 보낸 토큰이 유효한지 확인할 때 사용합니다.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 특정 유저에게 발급된 리프레시 토큰 정보를 조회합니다.
     * 로그인 시 기존에 발급된 토큰이 있는지 확인하여 갱신할 때 사용합니다.
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * 특정 유저의 리프레시 토큰을 삭제합니다.
     * 로그아웃 시나 회원 탈퇴 시 보안을 위해 토큰을 즉시 폐기할 때 사용합니다.
     */
    void deleteByUser(User user);
}