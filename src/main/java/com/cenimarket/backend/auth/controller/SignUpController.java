package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.service.SignUpService;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SignUpController {
    private final SignUpService signUpService;

    /*회원가입 API 최종적으로 완료*/

    @PostMapping("/signup/last")
    public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO) {
        // 서비스 계층의 가입 로직 호출
        SignUpResponseDTO response = signUpService.signUp(requestDTO);

        // HTTP 상태 코드 201(Created)와 함께 결과 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}