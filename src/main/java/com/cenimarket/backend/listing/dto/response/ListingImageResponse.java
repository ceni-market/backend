package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.ListingImage;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ListingImageResponse {
    private final Long id;
    private final String imageUrl;
    private final Integer sortOrder;

    public static ListingImageResponse from(ListingImage image) {
        return ListingImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .sortOrder(image.getSortOrder())
                .build();
    }
}
