package com.cenimarket.backend.auth.repository;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.domain.VerificationPurpose;
import com.cenimarket.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.http.HttpHeaders;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {


    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<EmailVerification> findTopByEmailAndTokenOrderByCreatedAtDesc(String email, String token);

    // 이메일, 토큰, 용도로 유효한 인증 정보 찾기
    Optional<EmailVerification> findByEmailAndTokenAndPurpose(String email, String token, VerificationPurpose purpose);

    // 아직 검증되지 않았고 만료되지 않은 특정 용도의 토큰이 있는지 확인
    Optional<EmailVerification> findByTokenAndPurposeAndVerifiedAtIsNull(String token, VerificationPurpose purpose);

    // 기존에 발급된 동일 용도의 토큰들 삭제 (새로운 요청 시 기존 것 무효화)
    void deleteByEmailAndPurpose(String email, VerificationPurpose purpose);

    Optional<EmailVerification> findTopByEmailAndTokenAndPurposeOrderByCreatedAtDesc(String email, String token, VerificationPurpose purpose);

    Optional<EmailVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, VerificationPurpose verificationPurpose);
}

