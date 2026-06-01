package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingQueryRepository extends JpaRepository<Listing, Long>, ListingQueryRepositoryCustom {
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
    // 모바일 검색
    @Query("""
      select l
      from Listing l
      where (:keyword is null
             or l.title like concat('%', :keyword, '%')
             or l.description like concat('%', :keyword, '%'))
        and (:categoryId is null or l.category.id = :categoryId)
        and (:type is null or l.type = :type)
  """)
    Page<Listing> findAllByCondition(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("type") ListingType type,
            Pageable pageable
    );

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

    // 전체 리스트 - 카테고리 타입 상태 필터링
    @Query("""
    select l
    from Listing l
    join fetch l.category c
    where (:type is null or l.type = :type)
      and (:categoryId is null or c.id = :categoryId)
      and (:status is null or l.status = :status)
""")
    Page<Listing> findAllByFilters(
            @Param("type") ListingType type,
            @Param("categoryId") Long categoryId,
            @Param("status") ListingStatus status,
            Pageable pageable
    );
}
