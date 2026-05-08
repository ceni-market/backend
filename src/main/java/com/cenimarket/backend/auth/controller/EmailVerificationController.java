package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.dto.request.EmailVerificationRequestDTO;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationConfirmResponseDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationResponseDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import com.cenimarket.backend.auth.service.EmailVerificationService;
import com.cenimarket.backend.auth.service.PasswordResetService;
import com.cenimarket.backend.auth.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    /*이메일 발송 API*/

    @PostMapping("/signup/email-request")
    public ResponseEntity<EmailVerificationResponseDTO> emailVerification(@RequestBody EmailVerificationRequestDTO request) {

        EmailVerificationResponseDTO response = emailVerificationService.sendVerificationEmail(request);

        // HTTP 상태 코드 201(Created)와 함께 결과 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/signup/verify")
    public ResponseEntity<EmailVerificationConfirmResponseDTO> verifyLink(
            @RequestParam String email,
            @RequestParam String token) {

        boolean isSuccess = emailVerificationService.confirmVerification(email, token);

        if (isSuccess) {
            return ResponseEntity.ok()
                    .body(EmailVerificationConfirmResponseDTO.success(email));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(EmailVerificationConfirmResponseDTO.fail(email));
        }
    }

    /**
     * 1. 인증 메일 발송 API
     */
    @PostMapping("/password-reset/email-request")
    public ResponseEntity<EmailVerificationResponseDTO> sendVerificationMail(@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = passwordResetService.sendResetLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. 비밀번호 재설정 토큰 검증 API
     * 메일 링크 클릭 시: GET /api/auth/password-reset/verify?email=...&token=...
     */
    /*@GetMapping("/password-reset/verify")
    public ResponseEntity<String> verifyPasswordReset(@RequestParam String email, @RequestParam String token) {
        // 서비스에서 토큰 유효성 검증 로직 수행
        passwordResetService.verifyResetToken(email, token);

        // 검증 성공 시, 프론트엔드의 "비밀번호 변경 입력 페이지"로 리다이렉트하거나 성공 메시지 반환
        return ResponseEntity.ok("인증에 성공했습니다. 비밀번호를 변경해 주세요.");
    }*/
}