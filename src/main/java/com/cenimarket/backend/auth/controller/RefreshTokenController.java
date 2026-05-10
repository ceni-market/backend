package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.request.RefreshTokenRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.dto.response.RefreshTokenResponseDTO;
import com.cenimarket.backend.auth.service.LoginService;
import com.cenimarket.backend.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // 보안 관례상 인증 관련 경로는 /api/auth로 묶는 것이 좋습니다.
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    /**
     * 리프레시 토큰 재발급 API
     * POST /api/auth/reissue
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> reissue(@RequestBody RefreshTokenRequestDTO request) {

        // 서비스 계층의 reissue 메서드를 호출하여 로직을 수행합니다.
        RefreshTokenResponseDTO response = refreshTokenService.reissue(request.getRefreshToken());

        // 새로운 AccessToken과 RefreshToken이 담긴 DTO를 반환합니다.
        return ResponseEntity.ok(response);
    }
}