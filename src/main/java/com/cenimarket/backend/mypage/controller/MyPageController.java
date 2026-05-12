package com.cenimarket.backend.mypage.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.dto.response.PageResponse;
import com.cenimarket.backend.mypage.dto.response.MyPageDashBoardResponse;
import com.cenimarket.backend.mypage.dto.response.MyPageInfoResponse;
import com.cenimarket.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    // 프로필 사진, 이름 반환
    @GetMapping("/api/mypage/me")
    public ResponseEntity<ApiResponse<MyPageInfoResponse>>
    getInfo(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(MyPageInfoResponse.from(user)));
    }

    @GetMapping("/api/mypage/summary")
    public ResponseEntity<ApiResponse<MyPageDashBoardResponse>>
    getDashBoard(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(myPageService.getDashBoard(user.getId())));
    }

    @GetMapping("/api/mypage/listings")
    public ResponseEntity<ApiResponse<PageResponse<ListingsListResponse>>> getMyListings
            (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
             Pageable pageable, @RequestParam(required = false) ListingType type,
             @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                myPageService.getMyListings(pageable, user.getId())
        )));
    }
}
