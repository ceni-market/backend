package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.request.ListingUpdateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
import com.cenimarket.backend.listing.dto.response.ListingDeleteResponse;
import com.cenimarket.backend.listing.dto.response.ListingUpdateResponse;
import com.cenimarket.backend.listing.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ApiResponse<ListingCreateResponse>> createListing(
            @Valid
            @RequestBody
            ListingCreateRequest request
    ) {
        ListingCreateResponse response =listingService.createListing(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{listingId}")
    public ResponseEntity<ApiResponse<ListingUpdateResponse>> updateListing(
            @PathVariable Long listingId,
            @Valid @RequestBody ListingUpdateRequest request
    ){
        ListingUpdateResponse response = listingService.updateListing(listingId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<ApiResponse<ListingDeleteResponse>> deleteListing(
            @PathVariable Long listingId
    ){
        ListingDeleteResponse response = listingService.deleteListing(listingId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
