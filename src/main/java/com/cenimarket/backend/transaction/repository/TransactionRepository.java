package com.cenimarket.backend.transaction.repository;

import com.cenimarket.backend.transaction.domain.Transaction;
import com.cenimarket.backend.transaction.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByChatRoomId(Long chatRoomId);
    boolean existsByListingId(Long listingId);
    long countByBuyerIdAndType(Long buyerId, TransactionType type);
}
