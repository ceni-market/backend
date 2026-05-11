package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {

}
