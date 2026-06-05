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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    //메시지를 DB에 저장하고, 채팅방의 마지막 메시지 데이터를 업데이트하는 메서드
    public void saveMessage(Long roomId, ChatMessageDto requestMessage){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다. 요청한 채팅방은 없습니다."));
        User sender = userRepository.findByEmail(requestMessage.getSenderEmail()).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다. 판매자 정보가 정확하지 않습니다."));
        //메시지로 조립
        ChatMessage message = ChatMessage.from(chatRoom, sender, requestMessage.getMessageType(), requestMessage.getMessage());
        //메시지 저장
        ChatMessage savedMessage = chatMessageRepository.save(message);
        //채팅방의 마지막 메시지 업데이트
        //채팅방의 마지막 활성화 시간 업데이트
        chatRoom.updateLastMessage(savedMessage);
    }

    //채팅방 생성 메서드
    public void createChatRoom(ChatRoomCreateRequest request) {
        User buyer = userRepository.findById(request.getBuyerId()).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다. 구매자 정보가 정확하지 않습니다."));
        User seller = userRepository.findById(request.getSellerId()).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다. 판매자 정보가 정확하지 않습니다."));
        Listing listing = listingRepository.findById(request.getListingId()).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다. 거래글 정보가 정확하지 않습니다."));
        //채팅방 생성(저장)
        ChatRoom newChatRoom = ChatRoom.from(buyer, seller, listing);
        chatRoomRepository.save(newChatRoom);
        //채팅 멤버 생성 메서드 호출
        createChatRoomMembers(newChatRoom, buyer, seller);
    }

    //채팅 멤버 생성 메서드
    public void createChatRoomMembers(ChatRoom chatRoom, User buyer, User seller) {
        //해당 채팅방에 대한 기존 멤버 엔티티가 없다면, buyer를 ChatRoomMember로 추가
        if(chatRoomMemberRepository.findByUserIdAndChatRoomId(buyer.getId(), chatRoom.getId()).isEmpty()){
            chatRoomMemberRepository.save(ChatRoomMember.from(buyer, chatRoom));
        }
        //해당 채팅방에 대한 기존 멤버 엔티티가 없다면,seller를 ChatRoomMember로 추가
        if(chatRoomMemberRepository.findByUserIdAndChatRoomId(seller.getId(), chatRoom.getId()).isEmpty()) {
            chatRoomMemberRepository.save(ChatRoomMember.from(seller, chatRoom));
        }

    }

    // 게시글에서 채팅 요청 시 기존 채팅방이 있는지 조회하는 메서드.
    public ChatRoomCreateResponse getExistChatRoom(ChatRoomCreateRequest request) { //채팅을 요청한 게시글이 바뀌면 ListingId가 바뀌도록 해야함.
        Long sellerId = request.getSellerId();                                      //지금은 Response DTO만 값이 바뀌고 있음. 이거 엔티티로 바꾸려면 계속 조회 해야되는데
        Long buyerId = request.getBuyerId();                                        //채팅방에 들어갈 때마다 조회하는거 너무 비효율적이라 어떡할지 모르겠음.
        Long listingId = request.getListingId();
        //판매자, 구매자 간 기존 채팅방 여부 조회
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId)
                .orElseGet(() -> chatRoomRepository.findBySellerIdAndBuyerId(buyerId, sellerId).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)));
        //기존 채팅방이 있는 상태에서 다른 물건의 게시글에서 채팅을 요청했을 때 채팅방이 참조하는 게시글 데이터 업데이트
        if(chatRoom.getListing().getId() != listingId) {
            chatRoom.updateListing(listingRepository.findById(listingId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "게시글 데이터가 정확하지 않습니다.")));
        }
        return ChatRoomCreateResponse.from(chatRoom, listingId);
    }

    //새로운 채팅 요청한 클라이언트의 채팅 멤버가 있는지 조회
    public boolean isMyChatRoomMember(Long chatRoomId, Long userId) {
        Optional<ChatRoomMember> member = chatRoomMemberRepository.findByUserIdAndChatRoomId(userId, chatRoomId);
        return member.isPresent();
    }

    //채팅방 다시 들어갈 때 ChatRoomMember를 만들어주는 메서드
    public void reJoinChatRoom(@NotNull Long chatRoomId, ChatRoomCreateRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채팅방이 없습니다."));
        User user = userRepository.findById(request.getBuyerId()).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "유저를 조회할 수 없습니다."));

        chatRoomMemberRepository.save(ChatRoomMember.from(user, chatRoom));
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
            int unreadCount = chatMessageRepository.countUnreadMessage(readAt, contactUser);
            chatRoomList.add(ChatRoomListResponse.getMyChatRoomData(chatRoomData, contactUser, unreadCount));
        }
        return chatRoomList;
    }

    //채팅방 내부에서 보여줄 채팅방 데이터 불러오는 메서드
    public ChatRoomListResponse getChatRoomDetails(UserPrincipal user, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "채팅방이 존재하지 않습니다."));
        User targetUser = userRepository.findById(chatRoom.getTargetUser(user.getId()).getId()).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "유저가 존재하지 않습니다."));
        Listing listing = listingRepository.findById(chatRoom.getListing().getId()).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "게시글이 존재하지 않습니다."));
        return ChatRoomListResponse.builder()
                .contactUserInfo(new ChatRoomListResponse.UserInfo(targetUser))
                .listingInfo(new ChatRoomListResponse.ListingInfo(listing))
                .build();
    }

    //채팅방의 기존 채팅 내역을 반환하는 메서드
    public List<ChatMessageDto> getChatHistory(UserPrincipal currentUser, Long chatRoomId){
        //현재 사용자가 해당 채팅방의 참가자인지 확인
        Long userId = currentUser.getId();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "채팅방을 찾을 수 없습니다."));
        if( !chatRoom.getSeller().getId().equals(userId) && !chatRoom.getBuyer().getId().equals(userId) ){
            throw new BusinessException(BUSINESS_ERROR, "접근 권한 없음.");
        }
        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(currentUser.getId(), chatRoomId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채팅 멤버를 찾을 수 없습니다."));
        // 채팅방의 멤버이면, 이전 채팅 데이터를 DTO로 변환하여 반환
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndCreatedAtAfterOrderByCreatedAtAsc(chatRoomId, member.getCreatedAt());
        List<ChatMessageDto> messageDtos = new ArrayList<>();
        for(ChatMessage message : messages){
            ChatMessageDto messageDto = ChatMessageDto.from(message.getContent(), message.getSender().getEmail(), message.getMessageType(), message.getCreatedAt());
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    //채팅방 나가기
    public void leaveChatRoom (Long userId, Long chatRoomId) {
        Long chatRoomMemberCount = chatRoomMemberRepository.countByChatRoomId(chatRoomId);
        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "이 채팅방에 참여하고 있지 않습니다."));
        if(chatRoomMemberCount >= 2){
            chatRoomMemberRepository.delete(member);
        } else {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new BusinessException(BUSINESS_ERROR, "해당 채팅방이 없습니다."));

            chatRoomMemberRepository.delete(member);
            chatRoomRepository.delete(chatRoom);
        }
    }

    //마지막 읽은 시간 업데이트 메서드
    public void updateReadAt(Long roomId, String senderEmail) {
        Long userId = userRepository.findByEmail(senderEmail).orElseThrow(()-> new BusinessException(ErrorCode.INVALID_INPUT_VALUE)).getId();
        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "해당 채팅방에 참여 중이지 않습니다.") );
        member.updateLastReadAt(LocalDateTime.now());
    }

}
