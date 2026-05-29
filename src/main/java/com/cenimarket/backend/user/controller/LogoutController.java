package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.service.LogoutService;
import com.cenimarket.backend.user.service.ProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 1. 유저 서비스에서 리프레시 토큰 삭제 로직 호출
        logoutService.logout(userPrincipal.getEmail());
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
