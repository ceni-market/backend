package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingQueryRepositoryCustom {
    Page<Listing> findAllBySearch(
            List<String> keywords,
            ListingType type,
            Long categoryId,
            ListingStatus status,
            Pageable pageable
    );
}
