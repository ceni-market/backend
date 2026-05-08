package com.cenimarket.backend.like.controller;

import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.like.dto.ListingLikeResponse;
import com.cenimarket.backend.like.service.ListingLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings/{listingId}/likes")
public class ListingLikeController {
    private final ListingLikeService listingLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ListingLikeResponse>> addLike(
            @PathVariable Long listingId,
            @RequestParam Long userId
    ){
        ListingLikeResponse response = listingLikeService.addLike(listingId, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<ListingLikeResponse>> deleteLike(
            @PathVariable Long listingId,
            @RequestParam Long userId
    ){
        ListingLikeResponse response =listingLikeService.removeLike(listingId, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
