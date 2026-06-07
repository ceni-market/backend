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
        //두 사람의 id가 같은 경우 오류 리턴
        if(request.getBuyerId().equals(request.getSellerId())){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "내 판매글 입니다. 채팅방을 생성할 수 없습니다.");
        }
        //기존에 존재하는 채팅방이 있는지 검사
        try{
            ChatRoomCreateResponse chatRoom = chatService.getExistChatRoom(request);
            //기존에 쓰던 채팅방이 이미 있다면 나와 상대가 모두 이미 채팅방에 들어와 있는지 검사
            boolean isMeInChatRoom = chatService.isInChatRoom(chatRoom.getChatRoomId(), request.getBuyerId());
            boolean isPartnerInChatRoom = chatService.isInChatRoom(chatRoom.getChatRoomId(), request.getSellerId());
            //내가 채팅방 나간 상태면 chatRoomMember 새로 생성
            if(!isMeInChatRoom){
                chatService.reJoinChatRoom(chatRoom.getChatRoomId(), request.getBuyerId());
            }
            //상대가 채팅방 나간 상태면 chatRoomMember 새로 생성
            if(!isPartnerInChatRoom){
                chatService.reJoinChatRoom(chatRoom.getChatRoomId(), request.getSellerId());
            }
            return ResponseEntity.ok(ApiResponse.ok(chatRoom));
        } catch (BusinessException e){
            //기존 채팅방이 없다면 채팅방 생성
            chatService.createChatRoom(request);
            return ResponseEntity.ok(ApiResponse.ok(chatService.getExistChatRoom(request)));
        }
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
