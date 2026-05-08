package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.domain.VerificationPurpose;
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

    // ==========================================
    // 1. 회원가입 (Signup) 관련 인증
    // ==========================================

    /**
     * 회원가입 인증 메일 발송
     * POST /api/auth/signup/email-request
     */
    @PostMapping("/signup/email-request")
    public ResponseEntity<EmailVerificationResponseDTO> requestSignupEmail(@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = emailVerificationService.sendVerificationEmail(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원가입 메일 링크 검증
     * GET /api/auth/signup/verify?email=...&token=...
     */
    @GetMapping("/signup/verify")
    public ResponseEntity<EmailVerificationConfirmResponseDTO> verifySignupLink(
            @RequestParam String email,
            @RequestParam String token) {

        boolean isSuccess = emailVerificationService.confirmVerification(email, token);

        return isSuccess
                ? ResponseEntity.ok(EmailVerificationConfirmResponseDTO.success(email))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EmailVerificationConfirmResponseDTO.fail(email));
    }

    // ==========================================
    // 2. 비밀번호 재설정 (Password Reset) 관련 인증
    // ==========================================

    /**
     * 비밀번호 재설정 메일 발송
     * POST /api/auth/password-reset/email-request
     */
    @PostMapping("/password-reset/email-request")
    public ResponseEntity<EmailVerificationResponseDTO> requestPasswordResetEmail(@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = passwordResetService.sendResetLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 비밀번호 재설정 메일 링크 검증
     * GET /api/auth/password-reset/verify?email=...&token=...
     */
    @GetMapping("/password-reset/verify")
    public ResponseEntity<EmailVerificationConfirmResponseDTO> verifyPasswordResetLink(
            @RequestParam String email,
            @RequestParam String token) {

        // 용도를 PASSWORD_RESET으로 명시하여 검증
        boolean isSuccess = passwordResetService.confirmVerification(email, token, VerificationPurpose.PASSWORDRESET);

        return isSuccess
                ? ResponseEntity.ok(EmailVerificationConfirmResponseDTO.success(email))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EmailVerificationConfirmResponseDTO.fail(email));
    }
}