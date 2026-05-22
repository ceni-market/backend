package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.Listing;
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
    public Page<ListingsListResponse> findAll(Pageable pageable, Long userId) {
        return listingQueryRepository.findAll(pageable)
                .map(listing -> toListResponse(listing, userId));
    }

    // 카테고리별 조회
    public Page<ListingsListResponse> findAllByCategory(Long categoryId, Pageable pageable, Long userId) {
        return listingQueryRepository.findAllByCategoryId(categoryId, pageable)
                .map(listing -> toListResponse(listing, userId));
    }

    // 판매/나눔 유형별 조회
    public Page<ListingsListResponse> findAllByType(ListingType type, Pageable pageable, Long userId) {
        return listingQueryRepository.findAllByType(type, pageable)
                .map(listing -> toListResponse(listing, userId));
    }

    // 카테고리와 판매/나눔 유형을 함께 적용한 조회
    public Page<ListingsListResponse> findAllByCategoryAndType(
            Long categoryId,
            ListingType type,
            Pageable pageable,
            Long userId
    ) {
        return listingQueryRepository.findAllByCategoryIdAndType(categoryId, type, pageable)
                .map(listing -> toListResponse(listing, userId));
    }

    // 내가 관심 등록한 게시글 조회
    public Page<ListingsListResponse> findLikedByUser(Pageable pageable, Long userId) {
        return listingLikeRepository.findLikedListingsByUserId(userId, pageable)
                .map(listing -> ListingsListResponse.from(listing, true));
    }

    // 상세 화면 조회 시 조회수도 함께 증가시킨다.
    public ListingDetailResponse findDetail(Long id) {
        Listing listing = listingQueryRepository.findListingById(id);
        listing.increaseViewCount();
        return ListingDetailResponse.from(listing);
    }

    // 수정 화면에 기존 값을 채울 때는 조회수를 증가시키지 않는다.
    @Transactional(readOnly = true)
    public ListingDetailResponse findDetailForEdit(Long id) {
        Listing listing = listingQueryRepository.findListingById(id);
        return ListingDetailResponse.from(listing);
    }

    private ListingsListResponse toListResponse(Listing listing, Long userId) {
        boolean likedByMe = listingLikeRepository.existsByUser_IdAndListing_Id(userId, listing.getId());

        return ListingsListResponse.from(listing, likedByMe);
    }

}
