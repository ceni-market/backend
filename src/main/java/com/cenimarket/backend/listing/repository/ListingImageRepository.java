package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    Optional<ListingImage> findFirstByListingIdOrderBySortOrderAsc(Long listingId);
}
