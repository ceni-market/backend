package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.dto.request.PasswordUpdateRequest;
import com.cenimarket.backend.user.dto.response.PasswordUpdateResponseDTO;
import com.cenimarket.backend.user.service.LogoutService;

import com.cenimarket.backend.user.service.PasswordUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user/password")
@RequiredArgsConstructor
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;

    @PostMapping("/update")
    public ResponseEntity<PasswordUpdateResponseDTO> updatePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PasswordUpdateRequest request) {

        // UserPrincipal에서 이메일을 추출하여 서비스에 전달
        passwordUpdateService.updatePassword(userPrincipal.getEmail(), request);

        return ResponseEntity.ok(PasswordUpdateResponseDTO.success());
    }
}