package com.cenimarket.backend.like.repository;

import com.cenimarket.backend.like.domain.ListingLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingLikeRepository extends JpaRepository<ListingLike, Long> {
    // 이미 관심 등록했는지 확인을 위한 조회
    boolean existsByUser_IdAndListing_Id(Long userId, Long listingId);
    // 관심 취소할때 기존 관심 row 조회
    Optional<ListingLike> findByUser_IdAndListing_Id(Long userId, Long listingId);
}
