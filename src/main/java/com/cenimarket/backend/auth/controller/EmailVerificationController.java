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

import java.util.HashMap;
import java.util.Map;

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
    @GetMapping(value = "/signup/verify", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> verifySignupLink(
            @RequestParam String email,
            @RequestParam String token) {

        boolean isSuccess = emailVerificationService.confirmVerification(email, token);

        if (isSuccess) {
            // 🎯 인증 성공 시 브라우저에 보여줄 예쁜 안내 및 3초 후 자동 닫기 스크립트
            String successHtml = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <title>이메일 인증 성공</title>
                    <style>
                        body { font-family: 'Apple SD Gothic Neo', sans-serif; text-align: center; padding-top: 100px; background-color: #f8f9fa; color: #333; }
                        .card { background: white; max-width: 450px; margin: 0 auto; padding: 40px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                        h1 { color: #0d6efd; font-size: 24px; margin-bottom: 10px; }
                        p { font-size: 15px; color: #666; line-height: 1.6; }
                        .spinner { display: inline-block; width: 20px; height: 20px; border: 3px solid rgba(13,110,253,0.2); border-top-color: #0d6efd; border-radius: 50%; animation: spin 1s linear infinite; margin-top: 15px; }
                        @keyframes spin { to { transform: rotate(360deg); } }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>✨ 이메일 인증 완료!</h1>
                        <p>인증이 성공적으로 완료되었습니다.<br>이 창은 <strong>3초 후</strong>에 자동으로 닫힙니다.</p>
                        <div class="spinner"></div>
                    </div>
                    
                    <script>
                        // 3초(3000ms) 후에 이 창을 자동으로 닫음
                        setTimeout(function() {
                            window.close();
                        }, 3000);
                    </script>
                </body>
                </html>
                """;
            return ResponseEntity.ok(successHtml);

        } else {
            // 인증 실패 시 안내 HTML
            String failHtml = """
                <!DOCTYPE html>
                <html lang="ko">
                <head><meta charset="UTF-8"><title>인증 실패</title></head>
                <body style="text-align:center; padding-top:100px; font-family:sans-serif;">
                    <h1 style="color:#dc3545;">❌ 인증 실패</h1>
                    <p>만료되었거나 유효하지 않은 인증 링크입니다. 다시 가입을 진행해주세요.</p>
                </body>
                </html>
                """;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failHtml);
        }
    }

    /**
     * 🎯 [신규 추가] 프론트엔드 폴링(Polling) 수신용 인증 상태 확인 API
     * GET /api/auth/signup/status?email=...
     */
    @GetMapping("/signup/status")
    public ResponseEntity<?> checkSignupStatus(@RequestParam String email) {
        // 서비스 레이어의 검증 확인 로직 호출 (메서드가 없다면 하단의 가이드 참고)
        boolean isVerified = emailVerificationService.isEmailVerified(email);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("email", email);
        responseBody.put("isVerified", isVerified);

        return ResponseEntity.ok(responseBody);
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
    @GetMapping(value = "/password-reset/verify", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> verifyPasswordResetLink(
            @RequestParam String email,
            @RequestParam String token) {

        boolean isSuccess = passwordResetService.confirmVerification(email, token, VerificationPurpose.PASSWORDRESET);

        if (isSuccess) {
            String successHtml = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <title>비밀번호 인증 성공</title>
                    <style>
                        body { font-family: 'Apple SD Gothic Neo', sans-serif; text-align: center; padding-top: 100px; background-color: #f8f9fa; color: #333; }
                        .card { background: white; max-width: 450px; margin: 0 auto; padding: 40px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                        h1 { color: #28a745; font-size: 24px; margin-bottom: 10px; }
                        p { font-size: 15px; color: #666; line-height: 1.6; }
                        .spinner { display: inline-block; width: 20px; height: 20px; border: 3px solid rgba(40,167,69,0.2); border-top-color: #28a745; border-radius: 50%; animation: spin 1s linear infinite; margin-top: 15px; }
                        @keyframes spin { to { transform: rotate(360deg); } }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>🔓 이메일 인증 완료!</h1>
                        <p>비밀번호 재설정을 위한 인증이 완료되었습니다.<br>기존 브라우저 탭으로 돌아가 비밀번호 변경을 완료해 주세요.</p>
                        <div class="spinner"></div>
                    </div>
                    <script>
                        setTimeout(function() { window.close(); }, 3000);
                    </script>
                </body>
                </html>
                """;
            return ResponseEntity.ok(successHtml);
        } else {
            String failHtml = """
                <!DOCTYPE html>
                <html lang="ko">
                <head><meta charset="UTF-8"><title>인증 실패</title></head>
                <body style="text-align:center; padding-top:100px; font-family:sans-serif;">
                    <h1 style="color:#dc3545;">❌ 인증 실패</h1>
                    <p>만료되었거나 유효하지 않은 비밀번호 재설정 링크입니다. 다시 요청해 주세요.</p>
                </body>
                </html>
                """;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failHtml);
        }
    }

    /**
     * 비밀번호 재설정 프론트엔드 폴링(Polling) 수신용 인증 상태 확인 API
     * GET /api/auth/password-reset/status?email=...
     */
    @GetMapping("/password-reset/status")
    public ResponseEntity<?> checkPasswordResetStatus(@RequestParam String email) {

        boolean isVerified = passwordResetService.isPasswordResetVerified(email); // 가입용 메서드와 엔티티가 목적별로 나뉘어 필터링된다면 정합합니다.

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("email", email);
        responseBody.put("isVerified", isVerified);
        return ResponseEntity.ok(responseBody);
    }
}