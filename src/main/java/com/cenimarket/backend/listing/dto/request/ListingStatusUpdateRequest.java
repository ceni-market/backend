package com.cenimarket.backend.listing.dto.request;

import com.cenimarket.backend.listing.domain.ListingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingStatusUpdateRequest {
    @NotNull
    private ListingStatus status;
}
