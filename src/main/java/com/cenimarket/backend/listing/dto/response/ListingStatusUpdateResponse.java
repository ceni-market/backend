package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.listing.domain.ListingStatus;

public record ListingStatusUpdateResponse (
        Long listingId,
        ListingStatus status
) {

}
