package com.cenimarket.backend.auth.service;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.domain.VerificationPurpose;
import com.cenimarket.backend.auth.dto.request.EmailVerificationRequestDTO;
import com.cenimarket.backend.auth.dto.request.PasswordResetRequestDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationResponseDTO;
import com.cenimarket.backend.auth.repository.EmailVerificationRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationR;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmailVerificationResponseDTO sendResetLink(EmailVerificationRequestDTO request) {
        // 1. DTO에서 목적(purpose) 확인 및 Enum 변환
        VerificationPurpose purpose = VerificationPurpose.valueOf(request.getPurpose().toUpperCase());

        // 1.5 재발송 제한 확인
        checkResendRestriction(request.getEmail());

        // 2. 가입된 유저인지 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 3. 기존에 해당 용도로 발급된 토큰 삭제 (중복 방지)
        emailVerificationR.deleteByEmailAndPurpose(user.getEmail(), purpose);

        // 4. 토큰 생성 및 DB 저장
        String token = UUID.randomUUID().toString();

        EmailVerification verification = EmailVerification.create(
                user.getEmail(),
                token,
                purpose
        );

        emailVerificationR.save(verification);
        mailService.sendVerificationMail(user.getEmail(), token, purpose);
        return EmailVerificationResponseDTO.from(verification);
    }

    @Transactional
    public boolean confirmVerification(String email, String token, VerificationPurpose purpose) {
        // 1. 해당 이메일, 토큰, 그리고 '정해진 용도'에 맞는 가장 최신 정보 조회
        EmailVerification verification = emailVerificationR
                .findTopByEmailAndTokenAndPurposeOrderByCreatedAtDesc(email, token, purpose)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 2. 이미 완료된 인증인 경우
        if (verification.getVerifiedAt() != null) {
            return true;
        }

        // 3. 만료 시간 검증
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 4. 인증 완료 처리
        verification.setVerifiedAt(LocalDateTime.now());
        return true;
    }

    @Transactional
    public String completePasswordReset(PasswordResetRequestDTO request) {
        // 1. 인증 도장이 찍힌 최신 토큰 조회
        EmailVerification verification = emailVerificationR
                .findTopByEmailAndTokenAndPurposeOrderByCreatedAtDesc(
                        request.getEmail(),
                        request.getToken(),
                        VerificationPurpose.PASSWORDRESET // Enum 명칭
                )
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // 2. 검증: 클릭을 통해 verifiedAt이 기록되었는지 확인
        if (verification.getVerifiedAt() == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 3. 실제 유저 조회 및 비밀번호 암호화 업데이트
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));

        // BCrypt 암호화 후 엔티티 업데이트 (Dirty Checking으로 자동 저장)
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        // 4. 보안을 위해 사용한 인증 데이터 삭제 (1회용 처리)
        emailVerificationR.delete(verification);

        // 완료 후 이메일 반환
        return user.getEmail();
    }


    private void checkResendRestriction(String email) {
        emailVerificationR.findTopByEmailOrderByCreatedAtDesc(email)
                .ifPresent(lastVerification -> {
                    // LocalDateTime.now()와 비교 시 시스템 시차 주의
                    if (lastVerification.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                        throw new BusinessException(ErrorCode.BUSINESS_ERROR);
                    }
                });
    }

    public boolean isPasswordResetVerified(String email) {
        // 1. 해당 이메일과 목적에 맞는 가장 최근의 인증 정보를 찾습니다.
        return emailVerificationR.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, VerificationPurpose.PASSWORDRESET)
                .map(verification -> verification.getVerifiedAt() != null) // 2. verifiedAt이 null이 아니면 인증 완료된 것
                .orElse(false); // 3. 데이터가 없으면 false
    }
}