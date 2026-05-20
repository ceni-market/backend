package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.category.service.CategoryService;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.request.ListingUpdateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
import com.cenimarket.backend.listing.dto.response.ListingDetailResponse;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.dto.response.ListingUpdateResponse;
import com.cenimarket.backend.listing.service.ListingQueryService;
import com.cenimarket.backend.listing.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MobileListingController {

    private final ListingQueryService listingQueryService;
    private final ListingService listingService;
    private final CategoryService categoryService;

    @GetMapping("/mobile/main")
    public String mainPage(
            @RequestParam(required = false)
            Long categoryId,
            @RequestParam(required = false)
            ListingType type,
            @RequestParam(defaultValue = "all")
            String tab,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        Page<ListingsListResponse> listings;
        Long userId = userPrincipal.getId();

        if ("likes".equals(tab)) {
            listings = listingQueryService.findLikedByUser(pageable, userId);
        } else if (categoryId == null && type == null) {
            listings = listingQueryService.findAll(pageable, userId);
        } else if (categoryId != null && type == null) {
            listings = listingQueryService.findAllByCategory(categoryId, pageable, userId);
        } else if (categoryId == null) {
            listings = listingQueryService.findAllByType(type, pageable, userId);
        } else {
            listings = listingQueryService.findAllByCategoryAndType(categoryId, type, pageable, userId);
        }

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("type", type);
        model.addAttribute("tab", tab);
        model.addAttribute("listings", listings.getContent());
        model.addAttribute("totalCount", listings.getTotalElements());

        return "main/index";
    }

    @GetMapping("/mobile/listings/write")
    public String listingWritePage(Model model) {
        model.addAttribute("categories", categoryService.getCategories());
        return "listing/write";
    }

    @PostMapping("/mobile/listings")
    public String createListing(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute ListingCreateRequest request
    ) {
        ListingCreateResponse response =
                listingService.createListing(userPrincipal.getId(), request);

        return "redirect:/mobile/listings/detail?id=" + response.listingId();
    }

    @GetMapping("/mobile/listings/edit")
    public String listingEditPage(
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        ListingDetailResponse listing = listingQueryService.findById(id);

        if (!listing.getSeller().getId().equals(userPrincipal.getId())) {
            return "redirect:/mobile/listings/detail?id=" + id;
        }

        model.addAttribute("listing", listing);
        model.addAttribute("categories", categoryService.getCategories());

        return "listing/edit";
    }

    @PostMapping("/mobile/listings/edit")
    public String updateListing(
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute ListingUpdateRequest request
    ) {
        ListingUpdateResponse response = listingService.updateListing(userPrincipal.getId(), id, request);

        return "redirect:/mobile/listings/detail?id=" + response.listingId();
    }

    @GetMapping("/mobile/listings/detail")
    public String listingDetailPage(
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        ListingDetailResponse listing = listingQueryService.findById(id);
        boolean isOwner = listing.getSeller().getId().equals(userPrincipal.getId());
        model.addAttribute("listing", listing);
        model.addAttribute("isOwner", isOwner);
        return "listing/detail";
    }
}
