package com.cenimarket.backend.chat.service;

import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.repository.ChatRoomRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.notification.domain.ChatNotification;
import com.cenimarket.backend.notification.domain.NotificationType;
import com.cenimarket.backend.notification.dto.response.ChatNotificationResponse;
import com.cenimarket.backend.notification.repository.NotificationRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WebSocketService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messageSendingOperations;

    public void createChatNoti(Long roomId, ChatMessageDto messageSendRequest) {
        //채팅방, 보낸유저, 받는유저, 채팅 내용, 채팅 타입,
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "채팅방이 없습니다."));
        User sender = userRepository.findByEmail(messageSendRequest.getSenderEmail()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "유저가 없습니다."));

        ChatNotification newChatNoti = ChatNotification.from(chatRoom, NotificationType.valueOf("CHAT"), sender, messageSendRequest.getMessage(), messageSendRequest.getMessageType());
        notificationRepository.save(newChatNoti);
        ChatNotificationResponse res = ChatNotificationResponse.from(newChatNoti);

        Long receiverId = chatRoom.getTargetUser(sender.getId()).getId();
        messageSendingOperations.convertAndSend("/queue/notification/" + receiverId, res);
    }
}
