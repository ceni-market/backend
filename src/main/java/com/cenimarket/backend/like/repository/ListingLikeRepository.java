package com.cenimarket.backend.like.repository;

import com.cenimarket.backend.like.domain.ListingLike;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
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

    @Query("""
        select ll.listing.id
        from ListingLike ll
        where ll.user.id = :userId
          and ll.listing.id in :listingIds
    """)
    List<Long> findLikedListingIds(
            @Param("userId") Long userId,
            @Param("listingIds") List<Long> listingIds
    );

    // 관심 취소할때 기존 관심 row 조회
    Optional<ListingLike> findByUser_IdAndListing_Id(Long userId, Long listingId);

    // 내 관심 상품 수
    Long countByUserId(Long userId);

    // 내 관심 상품 리스트 반환
    @Query(
            value = """
                        select l
                        from ListingLike ll
                        join ll.listing l
                        join fetch l.category
                        where ll.user.id = :userId
                          and (:type is null or l.type = :type)
                          and (:status is null or l.status = :status)
                        order by ll.id desc
                    """,
            countQuery = """
                        select count(ll)
                        from ListingLike ll
                        join ll.listing l
                        where ll.user.id = :userId
                          and (:type is null or l.type = :type)
                          and (:status is null or l.status = :status)
                    """
    )
    Page<Listing> findLikedListingsByUserId(
            @Param("userId") Long userId,
            @Param("type") ListingType type,
            @Param("status") ListingStatus status,
            Pageable pageable
    );

    // 내 관심 상품 목록에 검색어, 카테고리, 거래 유형 필터를 적용해 조회
    @Query(
            value = """
              select l
              from ListingLike ll
              join ll.listing l
              join fetch l.category
              where ll.user.id = :userId
                and (:keyword is null
                     or l.title like concat('%', :keyword, '%')
                     or l.description like concat('%', :keyword, '%'))
                and (:categoryId is null or l.category.id = :categoryId)
                and (:type is null or l.type = :type)
              order by ll.id desc
          """,
            countQuery = """
              select count(ll)
              from ListingLike ll
              join ll.listing l
              where ll.user.id = :userId
                and (:keyword is null
                     or l.title like concat('%', :keyword, '%')
                     or l.description like concat('%', :keyword, '%'))
                and (:categoryId is null or l.category.id = :categoryId)
                and (:type is null or l.type = :type)
          """
    )
    Page<Listing> findLikedListingsByCondition(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("type") ListingType type,
            Pageable pageable
    );
}
