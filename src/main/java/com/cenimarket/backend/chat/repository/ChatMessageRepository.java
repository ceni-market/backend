package com.cenimarket.backend.chat.repository;

import com.cenimarket.backend.chat.domain.ChatMessage;
import com.cenimarket.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoom);

    @Query("select count(*) from ChatMessage cm where cm.createdAt > :lastReadAt and cm.sender = :user")
    int countUnreadMessage(LocalDateTime lastReadAt, User user);
}
