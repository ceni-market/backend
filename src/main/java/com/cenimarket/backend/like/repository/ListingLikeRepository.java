package com.cenimarket.backend.like.repository;

import com.cenimarket.backend.like.domain.ListingLike;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ListingLikeRepository extends JpaRepository<ListingLike, Long> {
    // 이미 관심 등록했는지 확인을 위한 조회
    boolean existsByUser_IdAndListing_Id(Long userId, Long listingId);
    // 관심 취소할때 기존 관심 row 조회
    Optional<ListingLike> findByUser_IdAndListing_Id(Long userId, Long listingId);
    // 내 관심 상품 수
    Long countByUserId(Long userId);
    // 내 관심 상품 리스트 반환
    @Query("""
        select ll.listing
        from ListingLike ll
        join ll.listing l
        where ll.user.id = :userId
        order by ll.id desc
    """)
    Page<Listing> findLikedListingsByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
