package com.cenimarket.backend.like.dto;

public record ListingLikeResponse(
        Long listingId,
        Boolean liked,
        Integer likeCount
) {
}
