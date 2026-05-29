package com.cenimarket.backend.chat.dto.response;

import com.cenimarket.backend.chat.domain.ChatMessage;
import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.domain.MessageType;
import com.cenimarket.backend.global.util.TimeConvertUtil;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomListResponse { //채팅방 목록 조회

    private Long chatRoomId;

    private UserInfo contactUserInfo;

    private ListingInfo listingInfo;

    private ChatMessageInfo lastMessageInfo;

    private LocalDateTime lastMessageAt;

    private String lastMessageAtConvert;

    private int unReadMessageCount;

    @Builder
    ChatRoomListResponse (Long chatRoomId, UserInfo contactUserInfo, ListingInfo listingInfo, ChatMessageInfo lastMessageInfo, LocalDateTime lastMessageAt, String lastMessageAtConvert, int unReadMessageCount){
        this.chatRoomId = chatRoomId;
        this.contactUserInfo = contactUserInfo;
        this.listingInfo = listingInfo;
        this.lastMessageInfo = lastMessageInfo;
        this.lastMessageAt = lastMessageAt;
        this.lastMessageAtConvert = lastMessageAtConvert;
        this.unReadMessageCount = unReadMessageCount;
    }

    public static ChatRoomListResponse getMyChatRoomData(ChatRoom chatRoomData, User contactUser, int unreadCount) {
        return ChatRoomListResponse.builder()
                .chatRoomId(chatRoomData.getId())
                .contactUserInfo(new UserInfo(contactUser))
                .listingInfo(new ListingInfo(chatRoomData.getListing()))
                .lastMessageInfo(new ChatMessageInfo(chatRoomData.getLastMessage()))
                .lastMessageAt(chatRoomData.getLastMessageAt())
                .lastMessageAtConvert(TimeConvertUtil.convertTime(chatRoomData.getLastMessageAt()))
                .unReadMessageCount(unreadCount)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String name;
        private String email;
        private UserStatus status;
        private String profileImageUrl;

        public UserInfo(User user) {
            this.userId = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.profileImageUrl = user.getProfileImageUrl();
            this.status = user.getStatus();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ListingInfo {
        private Long id;
        private String title;
        private Integer price;

        public ListingInfo(Listing listing) {
            this.id = listing.getId();
            this.title = listing.getTitle();
            this.price = listing.getPrice();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ChatMessageInfo {
        private Long id;
        private MessageType messageType;
        private String content;

        public ChatMessageInfo(ChatMessage message) {
            this.id = message.getId();
            this.messageType = message.getMessageType();
            this.content = message.getContent();
        }
    }
}
