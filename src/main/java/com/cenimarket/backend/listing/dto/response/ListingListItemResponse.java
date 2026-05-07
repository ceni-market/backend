package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;

import java.time.LocalDateTime;

public record ListingListItemResponse(
        Long id,
        String title,
        Integer price,
        ListingType type,
        ListingStatus status,
        Long categoryId,
        String categoryName,
        String thumbnailUrl,
        Integer likeCount,
        LocalDateTime createdAt
) {
    public  static ListingListItemResponse from(Listing listing, String thumbnailUrl ) {
        return new ListingListItemResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getType(),
                listing.getStatus(),
                listing.getCategory() == null ? null : listing.getCategory().getId(),
                listing.getCategory() == null ? null : listing.getCategory().getName(),
                thumbnailUrl,
                listing.getLikeCount(),
                listing.getCreatedAt()
        );
    }
}

