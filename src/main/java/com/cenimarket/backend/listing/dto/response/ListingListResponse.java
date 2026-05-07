package com.cenimarket.backend.listing.dto.response;

import java.util.List;

public record ListingListResponse(
        List<ListingListItemResponse> content,
        PageInfoResponse pageInfo
) {
    public static ListingListResponse of(
            List<ListingListItemResponse> content,
            PageInfoResponse pageInfo
    ) {
        return new ListingListResponse(content, pageInfo);
    }

}