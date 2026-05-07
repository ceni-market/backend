package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    @Query("""
              SELECT l
              FROM Listing l
              WHERE l.deletedAt IS NULL
                AND (:type IS NULL OR l.type = :type)
                AND (:categoryId IS NULL OR l.category.id = :categoryId)
                AND (:keyword IS NULL OR l.title LIKE CONCAT('%', :keyword,'%'))
              """)
    Page<Listing> search(
            @Param("type") ListingType type,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
