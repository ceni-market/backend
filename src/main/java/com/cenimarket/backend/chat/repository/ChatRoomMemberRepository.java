package com.cenimarket.backend.chat.repository;

import com.cenimarket.backend.chat.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    Optional<ChatRoomMember> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
    List<ChatRoomMember> findByUserId(Long userId);
}