package com.cenimarket.backend.chat.dto.response;

import com.cenimarket.backend.chat.domain.ChatMessage;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomListResponse { //채팅방 목록 조회
    @NotNull
    private Long chatRoomId;
    @NotNull
    private User contactUser;

    private Listing listing;

    private ChatMessage lastMessage;

    private LocalDateTime lastMessageAt;

    private String lastMessageAtConvert;

    private int unReadMessageCount;

    @Builder
    ChatRoomListResponse (Long chatRoomId, User contactUser, Listing listing, ChatMessage lastMessage, LocalDateTime lastMessageAt, String lastMessageAtConvert, int unReadMessageCount){
        this.chatRoomId = chatRoomId;
        this.contactUser = contactUser;
        this.listing = listing;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.lastMessageAtConvert = lastMessageAtConvert;
        this.unReadMessageCount = unReadMessageCount;
    }

    public static ChatRoomListResponse from(Long chatRoomId, User contactUser, Listing listing, ChatMessage lastMessage, LocalDateTime lastMessageAt, String lastMessageAtConvert, int unReadMessageCount) {
        return ChatRoomListResponse.builder()
                .chatRoomId(chatRoomId)
                .contactUser(contactUser)
                .listing(listing)
                .lastMessage(lastMessage)
                .lastMessageAt(lastMessageAt)
                .lastMessageAtConvert(lastMessageAtConvert)
                .unReadMessageCount(unReadMessageCount)
                .build();
    }
}
