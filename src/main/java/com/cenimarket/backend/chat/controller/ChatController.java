package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.service.ChatService;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping("/mychat")
    public ResponseEntity<?> getMyChatRoom(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyChatRooms(principal)));
    }

    @PostMapping("/create")
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(@RequestBody Map<String, Object> data) {//맵에 listingId, sellerId 들어오는 중. Service로 넘기면서 바꿀 예정.
        System.out.println("구매자 이메일 추출 시작");
        String buyerEmail = (String)data.get("buyerEmail");
        System.out.println(buyerEmail);
        System.out.println("구매자 찾기 시작");
        User buyer = userRepository.findByEmail(buyerEmail).orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        System.out.println("구매자 ID 추출 시작");
        Long buyerId = buyer.getId();
        System.out.println("판매자 ID 추출 시작");
        Long sellerId = ((Number)data.get("sellerId")).longValue();
        //두 사람의 id가 같은 경우 오류 리턴.
        if(buyerId == sellerId){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "내 판매글 입니다. 채팅방을 생성할 수 없습니다.");
        }
        System.out.println("ChatRoomCreateRequestDTO 조립 시작");
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder() //DTO 내부로 넣어야함.
                .listingId(((Number)data.get("listingId")).longValue())
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
        System.out.println("완료");
        System.out.println("기존 채팅방이 있는지 검사 시작");
        try{
            ChatRoomCreateResponse chatRoom = chatService.getExistChatRoom(request);
            return ResponseEntity.ok(chatRoom);
        } catch (BusinessException e){
            System.out.println("기존 채팅방 없음. 새로운 채팅방 생성.");
            System.out.println("chatService.createChatRoom 실행");
            chatService.createChatRoom(request);
            System.out.println("완료");
            System.out.println("chatService.getExistChatRoom 실행");
            ChatRoomCreateResponse response = chatService.getExistChatRoom(request);
            System.out.println("완료");
            System.out.println("리턴");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/chatroom/{chatRoomId}")
    public String goToChatRoom(@PathVariable Long chatRoomId) {
        return "chatroom";
    }

    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getChatHistory(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserPrincipal user){
        return ResponseEntity.ok(ApiResponse.ok(chatService.getChatHistory(user, chatRoomId)));
    }

    @GetMapping("/{chatRoomId}/readAt")
    public void updateReadAt(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserPrincipal user) {
        chatService.updateReadAt(chatRoomId, user.getEmail());
//        return ResponseEntity.ok(ApiResponse.ok(""));
    }

    @DeleteMapping("/{chatRoomId}/delete")
    public ResponseEntity<?> leaveChatRoom(@AuthenticationPrincipal UserPrincipal userprincipal, @PathVariable Long chatRoomId){ //채팅방 나가기
        chatService.leaveChatRoom(userprincipal.getId(), chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok("채팅방 나가기 성공"));
    }

}
