package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.repository.ChatMessageRepository;
import com.cenimarket.backend.chat.service.ChatService;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @PostMapping("/mychat")
    @ResponseBody
    public ResponseEntity<?> getMyChatRoom(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyChatRoom(userId)));
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(@RequestBody Map<String, Object> data) {
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
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채팅방을 생성할 수 없습니다.");
        }
        System.out.println("ChatRoomCreateRequestDTO 조립 시작");
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .listingId(((Number)data.get("listingId")).longValue())
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
        System.out.println("완료");
        System.out.println("기존 채팅방이 있는지 검사 시작");
        try{
            ChatRoomCreateResponse chatRoom = chatService.getChatRoom(sellerId, buyerId);
            return ResponseEntity.ok(chatRoom);
        } catch (BusinessException e){
            System.out.println("기존 채팅방 없음. 새로운 채팅방 생성.");
            System.out.println("chatService.createChatRoom 실행");
            chatService.createChatRoom(request);
            System.out.println("완료");
            System.out.println("chatService.getChatRoom 실행");
            ChatRoomCreateResponse response = chatService.getChatRoom(sellerId, buyerId);
            System.out.println("완료");
            System.out.println("리턴");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{chatRoomId}")
    public String goToChatRoom(@PathVariable Long chatRoomId) {
        return "chatroom";
    }

    @PostMapping("/history/{chatRoomId}")
    @ResponseBody
    public List<ChatMessageDto> getChatHistory(@PathVariable Long chatRoomId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return chatService.getChatHistory(chatRoomId, userEmail);
    }

    @DeleteMapping("/{chatRoomId}/delete")
    public ResponseEntity<?> leaveChatRoom(@AuthenticationPrincipal UserPrincipal userprincipal, @PathVariable Long chatRoomId){ //채팅방 나가기
        chatService.leaveChatRoom(userprincipal.getId(), chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok("채팅방 나가기 성공"));
    }

}
