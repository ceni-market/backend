package com.cenimarket.backend.chat.repository;

import com.cenimarket.backend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySellerIdAndBuyerId(Long sellerId, Long buyerId);
}
