package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingQueryRepository extends JpaRepository<Listing, Long> {
    Listing findListingById(Long id);
    long countBySellerIdAndStatus(Long sellerId, ListingStatus status);
    Page<Listing> findAllBySellerId(Long sellerId, Pageable pageable);

    Page<Listing> findAllBySellerIdAndType(Long sellerId, ListingType type, Pageable pageable);
}
