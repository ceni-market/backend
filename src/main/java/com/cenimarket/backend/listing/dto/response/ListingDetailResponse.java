package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class ListingDetailResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer price;
    private final List<ListingImageResponse> images;
    private final CategoryResponse category;
    private final Integer likeCount;
    private final Integer viewCount;
    private final LocalDateTime updatedAt;
    private final ListingStatus status;
    private final ListingType type;
    private final SellerResponse seller;

    public static ListingDetailResponse from(Listing listing) {
        return ListingDetailResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .images(
                        listing.getImages().stream()
                                .map(ListingImageResponse::from)
                                .toList()
                )
                .category(CategoryResponse.from(listing.getCategory()))
                .likeCount(listing.getLikeCount())
                .viewCount(listing.getViewCount())
                .updatedAt(listing.getUpdatedAt())
                .status(listing.getStatus())
                .type(listing.getType())
                .seller(SellerResponse.from(listing.getSeller()))
                .build();
    }
}
