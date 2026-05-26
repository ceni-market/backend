package com.cenimarket.backend.like.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.like.dto.ListingLikeResponse;
import com.cenimarket.backend.like.service.ListingLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/listings/{listingId}/likes")
public class MobileListingLikeController {
    private final ListingLikeService listingLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ListingLikeResponse>> addLike(
            @PathVariable Long listingId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        ListingLikeResponse response = listingLikeService.addLike(listingId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<ListingLikeResponse>> deleteLike(
            @PathVariable Long listingId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        ListingLikeResponse response =listingLikeService.removeLike(listingId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}