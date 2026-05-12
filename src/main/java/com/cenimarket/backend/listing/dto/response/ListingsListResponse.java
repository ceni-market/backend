package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class ListingsListResponse {
    private final Long id;
    private final String title;
    private final Integer price;
    private final ListingImageResponse image;
    private final CategoryResponse category;
    private final Integer likeCount;
    private final LocalDateTime updatedAt;
    private final ListingStatus status;
    private final ListingType type;

    public static ListingsListResponse from(Listing listing) {
        return ListingsListResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .price(listing.getPrice())
                .image(
                        ListingImageResponse.from(
                                listing.getImages().getFirst()
                        )
                )
                .category(CategoryResponse.from(listing.getCategory()))
                .likeCount(listing.getLikeCount())
                .updatedAt(listing.getUpdatedAt())
                .status(listing.getStatus())
                .type(listing.getType())
                .build();
    }
}
