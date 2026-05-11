package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingQueryRepository extends JpaRepository<Listing, Long> {
    Listing findListingById(Long id);
}
