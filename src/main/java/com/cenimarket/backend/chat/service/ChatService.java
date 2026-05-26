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
import com.cenimarket.backend.global.util.TimeConvertUtil;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.repository.ListingRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.cenimarket.backend.global.error.ErrorCode.BUSINESS_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;


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
                .lastReadAt(LocalDateTime.now())
                .build();
        chatRoomMemberRepository.save(member1);
        System.out.println("완료");
        //seller를 ChatRoomMember로 추가
        System.out.println("seller 저장 중");
        ChatRoomMember member2 = ChatRoomMember.builder()
                .user(seller)
                .chatRoom(chatRoom)
                .lastReadAt(LocalDateTime.now())
                .build();
        chatRoomMemberRepository.save(member2);
        System.out.println("완료");
    }

    //채팅방 목록 생성에 필요한 데이터 불러오는 메서드
    public List<ChatRoomListResponse> getMyChatRooms(UserPrincipal user) {
        Long userId = user.getId();
        //ChatRoomMember 조회
        List<ChatRoomMember> members = chatRoomMemberRepository.findByUserId(userId);
        List<ChatRoomListResponse> chatRoomList = new ArrayList<>();
        User contactUser = null;
        //ChatRoomMember -> ChatRoom 조회
        for(ChatRoomMember member : members){
            ChatRoom myChatRoom = member.getChatRoom();
            contactUser = myChatRoom.getTargetUser(user.getId());
            ChatRoom chatRoomData = chatRoomRepository.findMyChatRoomsData(myChatRoom.getId());
            LocalDateTime readAt = member.getLastReadAt();
            System.out.println(readAt);
            int unreadCount = chatMessageRepository.countUnreadMessage(readAt);
            chatRoomList.add(ChatRoomListResponse
                    .from(chatRoomData.getId(),
                            contactUser,
                            chatRoomData.getListing(),
                            chatRoomData.getLastMessage(),
                            chatRoomData.getLastMessageAt(),
                            TimeConvertUtil.convertTime(chatRoomData.getLastMessageAt()),
                            unreadCount));
        }
        return chatRoomList;
    }

    //채팅방 내부에서 보여줄 채팅방 데이터 불러오는 메서드
    public ChatRoomListResponse getChatRoomDetails(UserPrincipal user, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "채팅방이 존재하지 않습니다."));
        User targetUser = userRepository.findById(chatRoom.getTargetUser(user.getId()).getId()).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "게시글이 존재하지 않습니다."));
        Listing listing = listingRepository.findById(chatRoom.getListing().getId()).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "게시글이 존재하지 않습니다."));
        return ChatRoomListResponse.builder()
                .contactUser(targetUser)
                .listing(listing)
                .build();
    }

    // 게시글에서 채팅 요청 시 기존 채팅방이 있는지 조회하는 메서드.
    public ChatRoomCreateResponse getExistChatRoom(ChatRoomCreateRequest request) { //채팅을 요청한 게시글이 바뀌면 ListingId가 바뀌도록 해야함.
        Long sellerId = request.getSellerId();                                      //지금은 Response DTO만 값이 바뀌고 있음. 이거 엔티티로 바꾸려면 계속 조회 해야되는데
        Long buyerId = request.getBuyerId();                                        //채팅방에 들어갈 때마다 조회하는거 너무 비효율적이라 어떡할지 모르겠음.
        Long listingId = request.getListingId();
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId)
                .orElseGet(() -> chatRoomRepository.findBySellerIdAndBuyerId(buyerId, sellerId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)));
        System.out.println("Service - chatRoom 조회 완료");
        if(chatRoom.getListing().getId() != listingId){
            ChatRoom currentChatRoom =chatRoomRepository.findById(chatRoom.getId()).orElseThrow();
            currentChatRoom.updateListing(listingRepository.findById(listingId).orElseThrow());//여기 두 개 채워야됌.
        }
        ChatRoomCreateResponse response = ChatRoomCreateResponse.builder()
                .chatRoomId(chatRoom.getId())
                .listingId(listingId)
                .sellerId(chatRoom.getSeller().getId())
                .buyerId(chatRoom.getBuyer().getId())
                .build();
        System.out.println("Service - response 조립 완료.");
        return response;
    }

    //채팅방의 기존 채팅 내역을 반환하는 메서드
    public List<ChatMessageDto> getChatHistory(UserPrincipal currentUser, Long chatRoomId){
        //현재 사용자가 해당 채팅방의 참가자인지 확인
        Long userId = currentUser.getId();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "채팅방을 찾을 수 없습니다."));
        if( !chatRoom.getSeller().getId().equals(userId) && !chatRoom.getBuyer().getId().equals(userId) ){
            throw new BusinessException(BUSINESS_ERROR, "접근 권한 없음.");
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
