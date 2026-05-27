package com.cenimarket.backend.mypage.service;

import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.listing.dto.response.ListingsListResponse;
import com.cenimarket.backend.listing.repository.ListingQueryRepository;
import com.cenimarket.backend.mypage.dto.response.MyPageDashBoardResponse;
import com.cenimarket.backend.mypage.dto.response.TransactionListResponse;
import com.cenimarket.backend.transaction.domain.TransactionRole;
import com.cenimarket.backend.transaction.domain.TransactionType;
import com.cenimarket.backend.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public Page<ListingsListResponse> getMyListings(Pageable pageable, Long id, ListingType type, ListingStatus status) {
        return listingQueryRepository.findAllBySellerIdAndTypeAndStatus(id, type, status, pageable).map(ListingsListResponse::from);
    }

    // 관심 상품 조회
    public Page<ListingsListResponse> getMyLikes(Pageable pageable, Long id, ListingType type, ListingStatus status) {
        return listingLikeRepository.findLikedListingsByUserId(id, type, status, pageable).map(ListingsListResponse::from);
    }

    // 최근 거래 내역 조회
    public Page<TransactionListResponse> getTransactions(Long id, TransactionRole role, Pageable pageable) {
        return transactionRepository.findMyTransactions(id, role.name() ,pageable)
                .map(transaction -> TransactionListResponse.from(transaction, id));
    }

    // 나눔 글 조회
    public Page<ListingsListResponse> getGiveaways(Pageable pageable, Long id) {
        return listingQueryRepository.findAllBySellerIdAndType(id, ListingType.GIVEAWAY, pageable)
                .map(ListingsListResponse::from);
    }

    // 검색
    public Page<ListingsListResponse> getSearch(Pageable pageable, String keyword) {
        return listingQueryRepository.search(keyword, pageable)
                .map(ListingsListResponse::from);
    }
}
