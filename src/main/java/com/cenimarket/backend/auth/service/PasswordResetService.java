package com.cenimarket.backend.auth.service;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.domain.VerificationPurpose;
import com.cenimarket.backend.auth.dto.request.EmailVerificationRequestDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationResponseDTO;
import com.cenimarket.backend.auth.repository.EmailVerificationRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public EmailVerificationResponseDTO sendResetLink(EmailVerificationRequestDTO request) {
        // 1. DTO에서 목적(purpose) 확인 및 Enum 변환
        VerificationPurpose purpose = VerificationPurpose.valueOf(request.getPurpose().toUpperCase());

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
        //verification.markAsVerified();
        return true;
    }


}