package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingDetailResponse;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.repository.ListingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ListingQueryService {
    private final ListingQueryRepository listingQueryRepository;
    private final ListingLikeRepository listingLikeRepository;

    //게시글 전체 조회
    public Page<ListingsListResponse> findAll(
            Pageable pageable,
            Long userId,
            ListingType type,
            Long categoryId,
            ListingStatus status
    ) {
        return listingQueryRepository.findAllByFilters(type, categoryId, status, pageable)
                .map(listing -> toListResponse(listing, userId));
    }

    // 관심 관계(ListingLike)를 기준으로 내가 관심 등록한 게시글에 검색어, 카테고리, 거래 유형 조건을 적용한다.
    public Page<ListingsListResponse> findLikedByCondition(
            String keyword,
            Long categoryId,
            ListingType type,
            Pageable pageable,
            Long userId
    ) {
        String normalizedKeyword = (keyword == null || keyword.isBlank())
                ? null
                : keyword.trim();

        return listingLikeRepository.findLikedListingsByCondition(
                        userId,
                        normalizedKeyword,
                        categoryId,
                        type,
                        pageable
                )
                .map(listing -> ListingsListResponse.from(listing, true));
    }

    // 게시글(Listing)을 기준으로 검색어, 카테고리, 거래 유형 조건을 적용한다.
    public Page<ListingsListResponse> findAllByCondition(
            String keyword,
            Long categoryId,
            ListingType type,
            Pageable pageable,
            Long userId
    ) {
        String normalizedKeyword = (keyword == null || keyword.isBlank())
                ? null
                : keyword.trim();

        return listingQueryRepository.findAllByCondition(
                        normalizedKeyword,
                        categoryId,
                        type,
                        pageable
                )
                .map(listing -> toListResponse(listing, userId));
    }

    // 상세 화면 조회 시 조회수도 함께 증가시킨다.
    public ListingDetailResponse findDetail(Long id, Long userId) {
        Listing listing = listingQueryRepository.findListingById(id);
        listing.increaseViewCount();
        boolean isOwner = listing.getSeller().getId().equals(userId);
        return ListingDetailResponse.from(listing, isOwner);
    }

    // 수정 화면에 기존 값을 채울 때는 조회수를 증가시키지 않는다.
    @Transactional(readOnly = true)
    public ListingDetailResponse findDetailForEdit(Long id) {
        Listing listing = listingQueryRepository.findListingById(id);
        return ListingDetailResponse.from(listing);
    }

    // 게시글 엔티티를 목록 응답 DTO로 변환하면서, 내가 관심 등록했는지도 확인한다.
    private ListingsListResponse toListResponse(Listing listing, Long userId) {
        boolean likedByMe = listingLikeRepository.existsByUser_IdAndListing_Id(userId, listing.getId());

        return ListingsListResponse.from(listing, likedByMe);
    }

}
