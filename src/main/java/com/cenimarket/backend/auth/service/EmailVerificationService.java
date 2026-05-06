package com.cenimarket.backend.auth.service;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.domain.VerificationPurpose;
import com.cenimarket.backend.auth.dto.request.EmailVerificationRequestDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationResponseDTO;
import com.cenimarket.backend.auth.repository.EmailVerificationRepository;
import com.cenimarket.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationR;
    private final MailService mailService;

    @Transactional
    public EmailVerificationResponseDTO sendVerificationEmail(EmailVerificationRequestDTO request) {

        // 1. 재발송 제한 확인
        checkResendRestriction(request.getEmail());

        // 2. 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();

        // 3. 엔티티 생성 (스태틱 팩토리 메서드 호출)
        // 만약 request.getPurpose()가 null일 경우를 대비해 예외처리가 필요할 수 있습니다.
        EmailVerification verification = EmailVerification.create(
                request.getEmail(),
                verificationToken,
                VerificationPurpose.valueOf(request.getPurpose().toUpperCase())
        );

        // 4. DB 저장
        EmailVerification savedVerification = emailVerificationR.save(verification);

        // 5. 실제 메일 발송 (주석 해제 시 변수명 확인)
        mailService.sendVerificationMail(request.getEmail(), verificationToken);

        return EmailVerificationResponseDTO.from(savedVerification);
    }

    private void checkResendRestriction(String email) {
        emailVerificationR.findTopByEmailOrderByCreatedAtDesc(email)
                .ifPresent(lastVerification -> {
                    // LocalDateTime.now()와 비교 시 시스템 시차 주의
                    if (lastVerification.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                        throw new IllegalStateException("인증 메일은 1분 후에 다시 요청할 수 있습니다.");
                    }
                });
    }
}