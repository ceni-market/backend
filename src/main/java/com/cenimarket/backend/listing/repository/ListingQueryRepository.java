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

    Page<Listing> findAllByCategoryId(Long categoryId, Pageable pageable);

    Page<Listing> findAllByType(ListingType type, Pageable pageable);

    Page<Listing> findAllByCategoryIdAndType(Long categoryId, ListingType type, Pageable pageable);

    // 내가 쓴 글 조회 - 타입과 상태에 맞게 반환
    @Query("""
    select l
    from Listing l
    join fetch l.category
    where l.seller.id = :sellerId
      and (:type is null or l.type = :type)
      and (:status is null or l.status = :status)
""")
    Page<Listing> findAllBySellerIdAndTypeAndStatus(
            @Param("sellerId") Long sellerId,
            @Param("type") ListingType type,
            @Param("status") ListingStatus status,
            Pageable pageable
    );
}
