package com.cenimarket.backend.transaction.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.transaction.domain.Transaction;
import com.cenimarket.backend.transaction.domain.TransactionRole;
import com.cenimarket.backend.transaction.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByChatRoomId(Long chatRoomId);
    boolean existsByListingId(Long listingId);

    // 내 최근 거래 내역 조회
    @Query(
            value = """
    select t
    from Transaction t
    join fetch t.listing l
    join fetch l.category
    where
        (
            (:role = 'ALL' and (t.seller.id = :userId or t.buyer.id = :userId))
            or (:role = 'SELLER' and t.seller.id = :userId)
            or (:role = 'BUYER' and t.buyer.id = :userId)
        )
        and (:type is null or t.type = :type)
    order by t.id desc
""",
            countQuery = """
                        select count(t)
                        from Transaction t
                        where
                            (
                                (:role = 'ALL' and (t.seller.id = :userId or t.buyer.id = :userId))
                                or (:role = 'SELLER' and t.seller.id = :userId)
                                or (:role = 'BUYER' and t.buyer.id = :userId)
                            )
                            and (:type is null or t.type = :type)
                    """
    )
    Page<Transaction> findMyTransactions(
            @Param("userId") Long userId,
            @Param("role") String role,
            @Param("type") TransactionType type,
            Pageable pageable
    );

    long countByBuyerIdOrSellerIdAndType(Long buyerId, Long sellerId, TransactionType type);
}
