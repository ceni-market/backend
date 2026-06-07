package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.service.ChatService;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    //내 채팅방 목록 조회 메서드
    @GetMapping("/mychat")
    public ResponseEntity<?> getMyChatRoom(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyChatRooms(principal)));
    }

    //1대1 채팅 요청 메서드
    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomCreateResponse>> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        if (request.getBuyerId().equals(request.getSellerId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "내 판매글 입니다. 채팅방을 생성할 수 없습니다.");
        }
        return ResponseEntity.ok(ApiResponse.ok(chatService.getOrCreateChatRoom(request)));
    }

    @GetMapping("/chatroom/{chatRoomId}")
    public String goToChatRoom(@PathVariable Long chatRoomId) {
        return "chatroom";
    }

    //기존 채팅 메시지 기록을 가져오는 메서드
    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getChatHistory(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserPrincipal user){
        return ResponseEntity.ok(ApiResponse.ok(chatService.getChatHistory(user, chatRoomId)));
    }

    //채팅 멤버의 마지막 조회 시간을 업데이트하는 메서드. 안 읽은 메시지 판별에 필요
    @GetMapping("/{chatRoomId}/readAt")
    public void updateReadAt(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserPrincipal user) {
        chatService.updateReadAt(chatRoomId, user.getEmail());
//        return ResponseEntity.ok(ApiResponse.ok(""));
    }

    //채팅방을 나가거나 삭제하는 메서드
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<String>> leaveChatRoom(@AuthenticationPrincipal UserPrincipal userprincipal, @PathVariable Long chatRoomId){ //채팅방 나가기
        chatService.leaveChatRoom(userprincipal.getId(), chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok("채팅방 나가기 성공"));
    }

}
