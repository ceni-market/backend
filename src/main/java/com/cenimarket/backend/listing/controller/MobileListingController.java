package com.cenimarket.backend.listing.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.category.dto.CategoryItemResponse;
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
import com.cenimarket.backend.upload.dto.ImageUploadResponse;
import com.cenimarket.backend.upload.service.ImageUploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MobileListingController {

    private final ListingQueryService listingQueryService;
    private final ListingService listingService;
    private final CategoryService categoryService;
    private final ImageUploadService imageUploadService;

    @GetMapping("/mobile/main")
    public String mainPage(
            @RequestParam(required = false)
            Long categoryId,
            @RequestParam(required = false)
            ListingType type,
            @RequestParam(required = false)
            String keyword,
            @RequestParam(defaultValue = "all")
            String tab,
            @RequestParam(defaultValue = "false")
            boolean fragment,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        Page<ListingsListResponse> listings;
        Long userId = userPrincipal.getId();

        if ("likes".equals(tab)) {
            listings = listingQueryService.findLikedByCondition(
                    keyword,
                    categoryId,
                    type,
                    pageable,
                    userId
            );
        } else {
            listings = listingQueryService.findAllByCondition(
                    keyword,
                    categoryId,
                    type,
                    pageable,
                    userId
            );
        }

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", tab);
        model.addAttribute("listings", listings.getContent());
        model.addAttribute("listingPage", listings);
        model.addAttribute("listCountText", createListCountText(tab, categoryId, listings.getTotalElements()));

        if (fragment) {
            return "main/index :: listingItems";
        }

        return "main/index";
    }

    private String createListCountText(String tab, Long categoryId, long totalCount) {
        String title = categoryId == null
                ? "전체"
                : categoryService.getCategories().stream()
                .filter(category -> category.id().equals(categoryId))
                .map(CategoryItemResponse::name)
                .findFirst()
                .orElse("전체");

        return title + " " + totalCount + "건";
    }

    @GetMapping("/mobile/listings/write")
    public String listingWritePage(Model model) {
        model.addAttribute("categories", categoryService.getCategories());
        return "listing/write";
    }

    @PostMapping("/mobile/listings")
    public String createListing(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute ListingCreateRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
            ImageUploadResponse uploadResponse = imageUploadService.uploadImages(images);
            request.setImageUrls(uploadResponse.getImageUrls());
        }

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
        ListingDetailResponse listing = listingQueryService.findDetailForEdit(id, userPrincipal.getId());
        model.addAttribute("listing", listing);
        model.addAttribute("categories", categoryService.getCategories());

        return "listing/edit";
    }

    @PostMapping("/mobile/listings/edit")
    public String updateListing(
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute ListingUpdateRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
            ImageUploadResponse uploadResponse = imageUploadService.uploadImages(images);
            request.setImageUrls(uploadResponse.getImageUrls());
        }

        ListingUpdateResponse response = listingService.updateListing(userPrincipal.getId(), id, request);

        return "redirect:/mobile/listings/detail?id=" + response.listingId();
    }

    @GetMapping("/mobile/listings/detail")
    public String listingDetailPage(
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        ListingDetailResponse listing = listingQueryService.findDetail(id, userPrincipal.getId());
        model.addAttribute("listing", listing);
        return "listing/detail";
    }
}
