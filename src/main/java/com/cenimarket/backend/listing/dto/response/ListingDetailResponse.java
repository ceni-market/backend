package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
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
    private final Integer price;
    private final List<ListingImageResponse> images;
    private final CategoryResponse category;
    private final Integer likeCount;
    private final LocalDateTime updatedAt;
    private final SellerResponse seller;

    public static ListingDetailResponse from(Listing listing) {
        return ListingDetailResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .price(listing.getPrice())
                .images(
                        listing.getImages().stream()
                                .map(ListingImageResponse::from)
                                .toList()
                )
                .category(CategoryResponse.from(listing.getCategory()))
                .likeCount(listing.getLikeCount())
                .updatedAt(listing.getUpdatedAt())
                .seller(SellerResponse.from(listing.getSeller()))
                .build();
    }
}
