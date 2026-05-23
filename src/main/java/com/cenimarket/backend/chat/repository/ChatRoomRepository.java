package com.cenimarket.backend.chat.repository;

import com.cenimarket.backend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySellerIdAndBuyerId(Long sellerId, Long buyerId);

    @Query("select distinct cr from ChatRoom cr " +
            "join fetch cr.listing l " +              // 1. 거래글 (ManyToOne) 패치조인
            "join fetch cr.lastMessage lm " +           // 2. 마지막 메시지 (ManyToOne) 패치조인
            "join fetch cr.buyer b " +             // 4. 진짜 상대방 유저 엔티티까지 패치조인
            "join fetch cr.seller s " +
            "where cr.id = :id " +                  // 5. 내가 참여한 방만 필터링
            "order by cr.lastMessageAt desc")             // 6. 최신 대화 순 정렬
    ChatRoom findMyChatRoomsData(@Param("id") Long crId);
}
