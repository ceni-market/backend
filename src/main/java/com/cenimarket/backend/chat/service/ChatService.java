package com.cenimarket.backend.chat.service;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.domain.ChatMessage;
import com.cenimarket.backend.chat.domain.ChatRoom;
import com.cenimarket.backend.chat.domain.ChatRoomMember;
import com.cenimarket.backend.chat.domain.MessageType;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.dto.response.ChatRoomListResponse;
import com.cenimarket.backend.chat.dto.response.LastChatMessageResponse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void saveMessage(Long roomId, ChatMessageDto messageSendRequest){  //채팅 저장
        //채팅방 객체 조회 (있는 경우만)
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        //보낸사람 객체 조회
        User sender = userRepository.findByEmail(messageSendRequest.getSenderEmail()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        //메시지 타입 조회
        MessageType type = messageSendRequest.getContentType();
        //메시지 저장
        //메시지 엔티티 조립
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .messageType(type)
                .content(messageSendRequest.getMessage())
                .build();
        //메시지 저장
        ChatMessage savedMessage = chatMessageRepository.save(message);
        //DTO로 필요한 데이터만 추출 (마지막 메시지 업데이트 용)
        LastChatMessageResponse messageDto = LastChatMessageResponse.of(savedMessage.getId(), savedMessage.getCreatedAt());
        //채팅방의 마지막 메시지 업데이트
        //채팅방의 마지막 활성화 시간 업데이트
        chatRoom.updateLastMessage(savedMessage, messageDto.getCreatedAt());
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

    public List<ChatRoomListResponse> getMyChatRoom(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        //ChatRoomMember 조회
        List<ChatRoomMember> members = chatRoomMemberRepository.findByUserId(userId);
        List<ChatRoomListResponse> chatRoomList = new ArrayList<>();
        User contactUser = null;
        //ChatRoomMember -> ChatRoom 조회
        for(ChatRoomMember member : members){
            ChatRoom myChatRoom = member.getChatRoom();
            ChatRoom chatRoomData = chatRoomRepository.findMyChatRoomsData(myChatRoom.getId());
            if(chatRoomData.getBuyer().getId() == userId) {
                contactUser = chatRoomData.getSeller();
            } else {
                contactUser = chatRoomData.getBuyer();
            }
            chatRoomList.add(ChatRoomListResponse
                    .from(chatRoomData.getId(),
                            contactUser,
                            chatRoomData.getListing(),
                            chatRoomData.getLastMessage(),
                            chatRoomData.getLastMessageAt(),
                            100));
        }
        return chatRoomList;
    }

    public Long getChatRoomId(Long sellerId, Long buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return chatRoom.getId();
    }

    public ChatRoomCreateResponse getExistChatRoom(Long sellerId, Long buyerId) {
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
                    .contentType(message.getMessageType())
                    .build();
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    public void leaveChatRoom (Long userId, Long chatRoomId) {
        Long chatRoomMemberCount = chatRoomMemberRepository.countByChatRoomId(chatRoomId);
        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "이 채팅방에 참여하고 있지 않습니다."));
        if(chatRoomMemberCount == 2){
            chatRoomMemberRepository.delete(member);
        } else {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "해당 채팅방이 없습니다."));

            chatRoomMemberRepository.delete(member);
            chatRoomRepository.delete(chatRoom);
        }
    }

    public void setLastReadAt(String userEmail, Long roomId) {
        Long userId = userRepository.findByEmail(userEmail).get().getId();
        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "해당 채팅방에 참여 중이지 않습니다.") );
        member.updateLastReadAt(LocalDateTime.now());
    }

}
