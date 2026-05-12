package com.cenimarket.backend.mypage.dto.response;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.dto.response.ListingImageResponse;
import com.cenimarket.backend.transaction.domain.Transaction;
import com.cenimarket.backend.transaction.domain.TransactionStatus;
import com.cenimarket.backend.transaction.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionListResponse {

    private final Long transactionId;
    private final Long listingId;

    private final String title;
    private final Integer price;
    private final String categoryName;
    private final ListingImageResponse image;

    private final Integer viewCount;
    private final Integer likeCount;

    private final TransactionStatus transactionStatus;
    private final TransactionType transactionType;
    private final LocalDateTime completedAt;

    // true -> 내가 판매한 거래 false -> 내가 구매한 거래
    private final boolean soldByMe;

    public static TransactionListResponse from(Transaction transaction, Long userId) {
        Listing listing = transaction.getListing();

        return TransactionListResponse.builder()
                .transactionId(transaction.getId())
                .listingId(listing.getId())
                .title(listing.getTitle())
                .price(transaction.getPrice())
                .categoryName(listing.getCategory().getName())
                .image(ListingImageResponse.from(listing.getImages().getFirst()))
                .viewCount(listing.getViewCount())
                .likeCount(listing.getLikeCount())
                .transactionStatus(transaction.getStatus())
                .transactionType(transaction.getType())
                .completedAt(transaction.getCompletedAt())
                .soldByMe(transaction.getSeller().getId().equals(userId))
                .build();
    }
}
