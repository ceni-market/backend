package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import com.cenimarket.backend.auth.service.LoginService;
import com.cenimarket.backend.auth.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO response = loginService.login(requestDTO);
        return ResponseEntity.ok(response);
    }
}