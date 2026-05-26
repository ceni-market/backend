package com.cenimarket.backend.mypage.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.dto.response.ProfileUpdateResponseDTO;
import com.cenimarket.backend.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.dto.response.ProfileUpdateResponseDTO;
import com.cenimarket.backend.user.service.ProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mobile")
@RequiredArgsConstructor
public class MobileMyPageController {

    private final ProfileService profileService;


    @GetMapping("/mypage")
    public String updateProfileImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 현재 로그인한 유저 정보
            , Model model
    ) {

        // 1. 로그인 정보가 없는 예외 상황 방어 체계 (보험용)
        if (userPrincipal == null) {
            return "redirect:/mobile/login";
        }
        // 2. 로그인된 사용자의 이메일 가져오기
        String email = userPrincipal.getEmail();
        String name = userPrincipal.getName();
        String profileImageUrl = userPrincipal.getProfileImageUrl();

        // 3. 타임리프 변수에 값 바인딩
        model.addAttribute("username", name);
        model.addAttribute("email", email);
        model.addAttribute("profileImageUrl", profileImageUrl);

        // 4. templates/mypage.html 화면 파일 렌더링
        return "mypage/index";
    }

    @PostMapping("/mypage/upload")
    public String updateProfileImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("profileImage") MultipartFile profileImage // HTML의 name="profileImage"
    ) {
        // 1. 비로그인 사용자 차단
        if (userPrincipal == null) {
            return "redirect:/mobile/login";
        }

        // 2. 파일이 비어있지 않은 경우에만 업로드 로직 수행
        if (!profileImage.isEmpty()) {
            // 외부 인프라(AWS S3) 파일 업로드 및 DB 회원 정보(이미지 URL) 갱신
            profileService.updateProfileImage(userPrincipal.getEmail(), profileImage);
        }

        // 3. 작업 완료 후, 마이페이지 화면으로 새로고침(리다이렉트)
        return "redirect:/mobile/mypage";
    }
}

