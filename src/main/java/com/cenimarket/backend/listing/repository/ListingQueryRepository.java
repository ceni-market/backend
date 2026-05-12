package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListingQueryRepository extends JpaRepository<Listing, Long> {
    Listing findListingById(Long id);
    long countBySellerIdAndStatus(Long sellerId, ListingStatus status);
    Page<Listing> findAllBySellerId(Long sellerId, Pageable pageable);

    Page<Listing> findAllBySellerIdAndType(Long sellerId, ListingType type, Pageable pageable);

    @Query("""
    select l
    from Listing l
    where l.title like concat('%', :keyword, '%')
       or l.description like concat('%', :keyword, '%')
""")
    Page<Listing> search(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
