package com.cenimarket.backend.mypage.controller;

import com.cenimarket.backend.mypage.dto.response.MemberResponseDTO;
import com.cenimarket.backend.mypage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDTO> getMyInfo() {
        // 서비스에서 현재 로그인한 유저 정보를 가져와 응답
        return ResponseEntity.ok(memberService.getMyInfoBySecurityContext());
    }
}
