package com.cenimarket.backend.transaction.service;

import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.repository.ChatRoomRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import com.cenimarket.backend.transaction.domain.Transaction;
import com.cenimarket.backend.transaction.domain.TransactionType;
import com.cenimarket.backend.transaction.dto.request.TransactionCompleteRequest;
import com.cenimarket.backend.transaction.dto.response.TransactionCompleteResponse;
import com.cenimarket.backend.transaction.repository.TransactionRepository;
import com.cenimarket.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ChatRoomRepository chatRoomRepository;

    public TransactionCompleteResponse createTransaction(Long sellerId, TransactionCompleteRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId()).orElseThrow();
        Listing listing = chatRoom.getListing();
        User seller = chatRoom.getSeller();
        User buyer = chatRoom.getBuyer();

        if (transactionRepository.existsByChatRoomId(chatRoom.getId())) {
            throw new IllegalArgumentException("이미 완료된 거래입니다.");
        }

        if (transactionRepository.existsByListingId(listing.getId())) {
            throw new IllegalArgumentException("이미 거래완료된 게시글입니다.");
        }

        if (!seller.getId().equals(sellerId)) {
            throw new IllegalArgumentException("판매자만 거래완료 처리할 수 있습니다.");
        }

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalArgumentException("거래 가능한 게시글이 아닙니다.");
        }

        LocalDateTime completedAt = LocalDateTime.now();
        if(listing.getType() == ListingType.SALE) {
            listing.completeSale(completedAt);
        } else if (listing.getType() == ListingType.GIVEAWAY){
            listing.completeGiveaway(completedAt);
        }

        Transaction transaction = Transaction.createCompleted(
                listing,
                seller,
                buyer,
                chatRoom,
                listing.getPrice(),
                TransactionType.valueOf(listing.getType().name()),
                completedAt
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionCompleteResponse.from(savedTransaction);
    }

}
