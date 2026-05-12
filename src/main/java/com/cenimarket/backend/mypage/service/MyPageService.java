package com.cenimarket.backend.mypage.service;

import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.repository.ListingQueryRepository;
import com.cenimarket.backend.mypage.dto.response.MyPageDashBoardResponse;
import com.cenimarket.backend.transaction.domain.TransactionType;
import com.cenimarket.backend.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final ListingQueryRepository listingQueryRepository;
    private final ListingLikeRepository listingLikeRepository;
    private final TransactionRepository transactionRepository;

    // 판매한 상품, 관심 상품, 나눔한 글, 나눔받은 글 - 순서대로
    public MyPageDashBoardResponse getDashBoard(Long userId) {
        long soldListingCount =
                listingQueryRepository.countBySellerIdAndStatus(userId, ListingStatus.SOLD);
        long likedListingCount =
                listingLikeRepository.countByUserId(userId);
        long donatedListingCount =
                listingQueryRepository.countBySellerIdAndStatus(userId, ListingStatus.GIVEN);
        long receivedDonationCount =
                transactionRepository.countByBuyerIdAndType(userId, TransactionType.GIVEAWAY);

        return MyPageDashBoardResponse.builder()
                .soldListingCount(soldListingCount)
                .likedListingCount(likedListingCount)
                .donatedListingCount(donatedListingCount)
                .receivedDonationCount(receivedDonationCount)
                .build();
    }

    // 내가 쓴 글 조회
    public Page<ListingsListResponse> getMyListings(Pageable pageable, Long id) {
        return listingQueryRepository.findAllBySellerId(id, pageable).map(ListingsListResponse::from);
    }

    // 관심 상품 조회
    public Page<ListingsListResponse> getMyLikes(Pageable pageable, Long id) {
        return listingLikeRepository.findLikedListingsByUserId(id, pageable).map(ListingsListResponse::from);
    }

}
