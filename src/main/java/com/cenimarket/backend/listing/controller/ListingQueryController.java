package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingDetailResponse;
import com.cenimarket.backend.listing.dto.response.ListingsPageResponse;
import com.cenimarket.backend.listing.service.ListingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ListingQueryController {
    private final ListingQueryService listingQueryService;

    // 게시글 목록 조회
    @GetMapping("/api/listings")
    public ResponseEntity<ApiResponse<ListingsPageResponse>> getListings
            (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
    Pageable pageable, @RequestParam(required = false) ListingType type) {
        return ResponseEntity.ok(ApiResponse.ok(ListingsPageResponse.from(
                listingQueryService.findAll(pageable)
        )));
    }

    // 게시글 상세 조회
    @GetMapping("/api/listings/{id}")
    public ResponseEntity<ApiResponse<ListingDetailResponse>> getListingDetail(
            @PathVariable("id") Long id ) {
        return ResponseEntity.ok(ApiResponse.ok(
                listingQueryService.findById(id)
        ));
    }

}
