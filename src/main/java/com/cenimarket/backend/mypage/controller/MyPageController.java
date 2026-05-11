package com.cenimarket.backend.mypage.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.mypage.dto.response.MyPageDashBoardResponse;
import com.cenimarket.backend.mypage.dto.response.MyPageInfoResponse;
import com.cenimarket.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    // 프로필 사진, 이름 반환
    @GetMapping("/api/mypage/me")
    public MyPageInfoResponse getInfo(@AuthenticationPrincipal UserPrincipal user) {
        return MyPageInfoResponse.from(user);
    }

    @GetMapping("/api/myapge/summary")
    public MyPageDashBoardResponse getDashBoard(@AuthenticationPrincipal UserPrincipal user) {
        return myPageService.getDashBoard(user.getId());
    }
}
