package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.listing.dto.response.ListingDetailResponse;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.service.ListingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MobileListingController {

    private final ListingQueryService listingQueryService;

    @GetMapping("/mobile/main")
    public String mainPage(
            @RequestParam(required = false)
            Long categoryId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        Page<ListingsListResponse> listings;

        if (categoryId == null) {
            listings = listingQueryService.findAll(pageable);
        } else {
            listings = listingQueryService.findAllByCategory(categoryId, pageable);
        }

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("listings", listings.getContent());
        model.addAttribute("totalCount", listings.getTotalElements());

        return "main/index";
    }

    @GetMapping("/mobile/listings/write")
    public String listingWritePage() {
        return "listing/write";
    }

    @GetMapping("/mobile/listings/detail")
    public String listingDetailPage(
            @RequestParam Long id,
            Model model
    ) {
        ListingDetailResponse listing = listingQueryService.findById(id);
        model.addAttribute("listing", listing);
        return "listing/detail";
    }
}