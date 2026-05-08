package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.dto.request.PasswordResetRequestDTO;
import com.cenimarket.backend.auth.dto.response.PasswordResetResponseDTO;
import com.cenimarket.backend.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * 3. 최종 비밀번호 변경 실행
     * POST /api/auth/password-reset/complete
     */
    @PostMapping("/password-reset/complete")
    public ResponseEntity<PasswordResetResponseDTO> passwordReset(
                                                 @RequestBody @Valid PasswordResetRequestDTO request) {

        // 서비스에서 토큰 검증 + 비밀번호 암호화 + DB 업데이트 처리
        passwordResetService.completePasswordReset(request);

        return ResponseEntity.ok(PasswordResetResponseDTO.success(request.getEmail()));
    }
}