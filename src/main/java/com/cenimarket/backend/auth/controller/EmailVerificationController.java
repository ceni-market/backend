package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.auth.dto.request.EmailVerificationRequestDTO;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.EmailVerificationResponseDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import com.cenimarket.backend.auth.service.EmailVerificationService;
import com.cenimarket.backend.auth.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    /*이메일 발송 API*/

    @PostMapping("/email-verification")
    public ResponseEntity<EmailVerificationResponseDTO> emailVerification(@RequestBody EmailVerificationRequestDTO requestDTO) {

        EmailVerificationResponseDTO response = emailVerificationService.sendVerificationEmail(requestDTO);

        // HTTP 상태 코드 201(Created)와 함께 결과 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}