package com.cenimarket.backend.mypage.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.dto.response.PageResponse;
import com.cenimarket.backend.mypage.dto.response.MyPageDashBoardResponse;
import com.cenimarket.backend.mypage.dto.response.MyPageInfoResponse;
import com.cenimarket.backend.mypage.dto.response.TransactionListResponse;
import com.cenimarket.backend.mypage.service.MyPageService;
import com.cenimarket.backend.transaction.domain.TransactionRole;
import com.cenimarket.backend.transaction.domain.TransactionType;
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

    // 프로필 사진, 이름 정보 반환
    @GetMapping("/api/mypage/me")
    public ResponseEntity<ApiResponse<MyPageInfoResponse>>
    getInfo(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(MyPageInfoResponse.from(user)));
    }

    // 대시보드 정보 반환 - 판매상품, 관심상품, 나눔한 글, 나눔받은 글
    @GetMapping("/api/mypage/summary")
    public ResponseEntity<ApiResponse<MyPageDashBoardResponse>>
    getDashBoard(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(myPageService.getDashBoard(user.getId())));
    }

    // 내가 쓴 글 리스트 반환
    @GetMapping("/api/mypage/listings")
    public ResponseEntity<ApiResponse<PageResponse<ListingsListResponse>>> getMyListings
    (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
     Pageable pageable, @AuthenticationPrincipal UserPrincipal user,
     @RequestParam(required = false) ListingType type, @RequestParam(required = false) ListingStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                myPageService.getMyListings(pageable, user.getId(), type, status)
        )));
    }

    // 관심상품 리스트 반환
    @GetMapping("/api/mypage/likes")
    public ResponseEntity<ApiResponse<PageResponse<ListingsListResponse>>> getMyLikes
    (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
     Pageable pageable, @AuthenticationPrincipal UserPrincipal user,
     @RequestParam(required = false) ListingType type, @RequestParam(required = false) ListingStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                myPageService.getMyLikes(pageable, user.getId(), type, status)
        )));
    }

    // 최근 거래 내역 조회
    @GetMapping("/api/mypage/transactions")
    public ResponseEntity<ApiResponse<PageResponse<TransactionListResponse>>> getTransactions(
            @RequestParam(defaultValue = "ALL") TransactionRole role,
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(myPageService.getTransactions(user.getId(), role, type, pageable))
        ));
    }

    // 나눔중인 글 반환
    @GetMapping("/api/mypage/giveaways")
    public ResponseEntity<ApiResponse<PageResponse<ListingsListResponse>>> getGiveaways
    (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
     Pageable pageable, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                myPageService.getGiveaways(pageable, user.getId())
        )));
    }

    // 검색한 글
    @GetMapping("/api/search")
    public ResponseEntity<ApiResponse<PageResponse<ListingsListResponse>>> getSearch
    (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
     Pageable pageable, @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                myPageService.getSearch(pageable, keyword)
        )));
    }
}
