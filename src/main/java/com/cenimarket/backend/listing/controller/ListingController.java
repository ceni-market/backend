package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.request.ListingStatusUpdateRequest;
import com.cenimarket.backend.listing.dto.request.ListingUpdateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
import com.cenimarket.backend.listing.dto.response.ListingDeleteResponse;
import com.cenimarket.backend.listing.dto.response.ListingStatusUpdateResponse;
import com.cenimarket.backend.listing.dto.response.ListingUpdateResponse;
import com.cenimarket.backend.listing.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;
    //게시글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ListingCreateResponse>> createListing(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid
            @RequestBody
            ListingCreateRequest request
    ) {
        ListingCreateResponse response =listingService.createListing(userPrincipal.getId(),request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
    //게시글 수정
    @PutMapping("/{listingId}")
    public ResponseEntity<ApiResponse<ListingUpdateResponse>> updateListing(
            @PathVariable Long listingId,
            @Valid @RequestBody ListingUpdateRequest request
    ){
        ListingUpdateResponse response = listingService.updateListing(listingId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
    //게시글 삭제
    @DeleteMapping("/{listingId}")
    public ResponseEntity<ApiResponse<ListingDeleteResponse>> deleteListing(
            @PathVariable Long listingId
    ){
        ListingDeleteResponse response = listingService.deleteListing(listingId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
    //게시글 상태변경
    @PatchMapping("/{listingId}/status")
    public ResponseEntity<ApiResponse<ListingStatusUpdateResponse>> updateListingStatus(
            @PathVariable Long listingId,
            @Valid @RequestBody ListingStatusUpdateRequest request
    ){
        ListingStatusUpdateResponse response = listingService.updateListingStatus(listingId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
