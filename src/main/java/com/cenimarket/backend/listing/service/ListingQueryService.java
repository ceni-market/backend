package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingDetailResponse;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.repository.ListingQueryRepository;
import com.cenimarket.backend.listing.search.SearchKeywordDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ListingQueryService {
    private final ListingQueryRepository listingQueryRepository;
    private final ListingLikeRepository listingLikeRepository;
    private final SearchKeywordDictionary searchKeywordDictionary;

    //게시글 전체 조회
    public Page<ListingsListResponse> findAll(
            Pageable pageable,
            Long userId,
            ListingType type,
            Long categoryId,
            ListingStatus status
    ) {
        Page<Listing> listingPage = listingQueryRepository.findAllByFilters(type, categoryId, status, pageable);
        Set<Long> likedListingIds = getLikedListingIds(userId, listingPage.getContent());

        return listingPage.map(listing ->
                ListingsListResponse.from(listing, likedListingIds.contains(listing.getId()))
        );
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

        Page<Listing> listingPage = listingQueryRepository.findAllByCondition(
                        normalizedKeyword,
                        categoryId,
                        type,
                        pageable
                );
        Set<Long> likedListingIds = getLikedListingIds(userId, listingPage.getContent());

        return listingPage.map(listing ->
                ListingsListResponse.from(listing, likedListingIds.contains(listing.getId()))
        );
    }

    // 상세 화면 조회 시 조회수도 함께 증가시킨다.
    public ListingDetailResponse findDetail(Long id, Long userId) {
        Listing listing = listingQueryRepository.findListingById(id);
        listing.increaseViewCount();
        boolean isOwner = userId != null && listing.getSeller().getId().equals(userId);
        boolean likedByMe = userId != null && listingLikeRepository.existsByUser_IdAndListing_Id(userId, listing.getId());
        return ListingDetailResponse.from(listing, isOwner, likedByMe);
    }

    // 수정 화면에 기존 값을 채울 때는 조회수를 증가시키지 않는다.
    @Transactional(readOnly = true)
    public ListingDetailResponse findDetailForEdit(Long id, Long userId) {
        Listing listing = listingQueryRepository.findListingById(id);

        if (!listing.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        return ListingDetailResponse.from(listing);
    }

    private Set<Long> getLikedListingIds(Long userId, List<Listing> listings) {
        if (userId == null || listings.isEmpty()) {
            return Set.of();
        }

        List<Long> listingIds = listings.stream()
                .map(Listing::getId)
                .toList();

        return new HashSet<>(listingLikeRepository.findLikedListingIds(userId, listingIds));
    }

    //게시글 검색
    public Page<ListingsListResponse> findSearch(
            Pageable pageable,
            String keyword,
            ListingType type,
            Long categoryId,
            ListingStatus status
    ) {
        List<String> keywords = searchKeywordDictionary.expand(keyword);
        return listingQueryRepository.findAllBySearch(keywords, type, categoryId, status, pageable)
                .map(listing -> ListingsListResponse.from(listing, false));
    }

}
