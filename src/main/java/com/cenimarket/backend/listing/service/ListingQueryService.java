package com.cenimarket.backend.listing.service;

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
}
