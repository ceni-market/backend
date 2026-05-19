package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.listing.domain.Listing;
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

    //게시글 전체 조회
    public Page<ListingsListResponse> findAll(Pageable pageable) {
        return listingQueryRepository.findAll(pageable).map(ListingsListResponse::from);
    }

    // 카테고리별 조회
    public Page<ListingsListResponse> findAllByCategory(Long categoryId, Pageable pageable) {
        return listingQueryRepository.findAllByCategoryId(categoryId, pageable)
                .map(ListingsListResponse::from);
    }

    // 상세 조회 시 조회수도 함께 증가시킨다.
    public ListingDetailResponse findById(Long id) {
        Listing listing = listingQueryRepository.findListingById(id);
        listing.increaseViewCount();
        return ListingDetailResponse.from(listing);
    }

}
