package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
import com.cenimarket.backend.listing.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
