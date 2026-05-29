package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.user.dto.response.ImageUploadResponseDTO;
import com.cenimarket.backend.user.service.ProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @PostMapping("/imageUpdate")
    public ResponseEntity<ImageUploadResponseDTO> updateProfileImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 현재 로그인한 유저 정보
            @RequestParam("file") MultipartFile file) {

        // 로그인된 사용자의 이메일 가져오기
        String email = userPrincipal.getEmail();

        String newImageUrl = profileService.updateProfileImage(email, file);

        return ResponseEntity.ok(new ImageUploadResponseDTO(List.of(newImageUrl)));
    }
}
