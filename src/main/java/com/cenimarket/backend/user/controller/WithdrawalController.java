package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.dto.request.WithdrawalRequestDTO;
import com.cenimarket.backend.user.dto.response.WithdrawalResponseDTO;
import com.cenimarket.backend.user.service.LogoutService;

import com.cenimarket.backend.user.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class WithdrawalController {

    private final LogoutService logoutService;
    private final WithdrawalService withdrawalService;

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalResponseDTO> withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody WithdrawalRequestDTO request) { // 탈퇴 시 비밀번호 확인용 DTO

        withdrawalService.withdraw(userPrincipal.getEmail(), request.getPassword());

        return ResponseEntity.ok(WithdrawalResponseDTO.success());
    }
}
