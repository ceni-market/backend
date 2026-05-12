package com.cenimarket.backend.chat.service;

import com.cenimarket.backend.chat.domain.ChatMessage;
import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.domain.ChatRoomMember;
import com.cenimarket.backend.chat.domain.MessageType;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.repository.ChatMessageRepository;
import com.cenimarket.backend.chat.repository.ChatRoomMemberRepository;
import com.cenimarket.backend.chat.repository.ChatRoomRepository;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.repository.ListingRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.cenimarket.backend.global.error.ErrorCode.BUSINESS_ERROR;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatRoomMemberRepository chatRoomMemberRepository, ChatMessageRepository chatMessageRepository, UserRepository userRepository, ListingRepository listingRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    public void saveMessage(Long roomId, ChatMessageDto MessageSendRequest){
        //채팅방 객체 조회 (있는 경우만)
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        //보낸사람 객체 조회
        User sender = userRepository.findByEmail(MessageSendRequest.getSenderEmail()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        //메시지 저장
        //메시지 엔티티 조립
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .messageType(MessageType.TEXT)
                .content(MessageSendRequest.getMessage())
                .build();
        //메시지 저장
        chatMessageRepository.save(message);
    }

    public void createChatRoom(ChatRoomCreateRequest request) {
        //유저 조회
        System.out.println("채팅방 생성을 위한 유저 조회 시작");
        User buyer = userRepository.findById(request.getBuyerId()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        System.out.println("완료");
        System.out.println("채팅방 생성을 위한 유저 조회 시작");
        User seller = userRepository.findById(request.getSellerId()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        System.out.println("완료");
        System.out.println("채팅방 생성을 위한 게시글 조회 시작");
        Listing listing = listingRepository.findById(request.getListingId()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        System.out.println("완료");
        //채팅방 생성(저장)
        System.out.println("채팅방 저장 중");
        ChatRoom chatRoom = ChatRoom.builder()
                .listing(listing)
                .seller(seller)
                .buyer(buyer)
                .build();
        chatRoomRepository.save(chatRoom);
        System.out.println("완료");
        //ChatRoomMember 생성
        //buyer를 ChatRoomMember로 추가
        System.out.println("buyer 저장 중");
        ChatRoomMember member1 = ChatRoomMember.builder()
                .user(buyer)
                .chatRoom(chatRoom)
                .build();
        chatRoomMemberRepository.save(member1);
        System.out.println("완료");
        //seller를 ChatRoomMember로 추가
        System.out.println("seller 저장 중");
        ChatRoomMember member2 = ChatRoomMember.builder()
                .user(seller)
                .chatRoom(chatRoom)
                .build();
        chatRoomMemberRepository.save(member2);
        System.out.println("완료");
    }

    public Long getChatRoomId(Long sellerId, Long buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return chatRoom.getId();
    }

    public ChatRoomCreateResponse getChatRoom(Long sellerId, Long buyerId) {
        ChatRoom chatRoom= chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        System.out.println("Service - chatRoom 조회 완료");
        ChatRoomCreateResponse response = ChatRoomCreateResponse.builder()
                .chatRoomId(chatRoom.getId())
                .listingId(chatRoom.getListing().getId())
                .sellerId(chatRoom.getSeller().getId())
                .buyerId(chatRoom.getBuyer().getId())
                .build();
        System.out.println("Service - response 조립 완료.");
        return response;
    }

    public List<ChatMessageDto> getChatHistory(Long chatRoomId, String currentUserEmail){
        //현재 사용자가 해당 채팅방의 참가자인지 확인
        System.out.println("현재 로그인 된 유저의 이메일은 " + currentUserEmail);
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow();
        Long userId = user.getId();
        System.out.println("현재 로그인 된 유저의 ID는 " + userId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        if( !chatRoom.getSeller().getId().equals(userId) && !chatRoom.getBuyer().getId().equals(userId) ){
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "접근 권한 없음.");
        }
        // 채팅방의 멤버이면, 이전 채팅 데이터를 DTO로 변환하여 반환
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        List<ChatMessageDto> messageDtos = new ArrayList<>();
        for(ChatMessage message : messages){
            ChatMessageDto messageDto = ChatMessageDto.builder()
                    .message(message.getContent())
                    .senderEmail(message.getSender().getEmail())
                    .build();
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }
}
