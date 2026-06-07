package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.ChatMessageDto;
import com.cenimarket.backend.chat.dto.request.ChatRoomCreateRequest;
import com.cenimarket.backend.chat.dto.response.ChatRoomCreateResponse;
import com.cenimarket.backend.chat.dto.response.ChatRoomListResponse;
import com.cenimarket.backend.chat.service.ChatService;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mobile/chat")
public class MobileChatController {

    private final ChatService chatService;

    @GetMapping
    public String chatPage(@AuthenticationPrincipal UserPrincipal user, Model model) {
        List<ChatRoomListResponse> myChatRooms = chatService.getMyChatRooms(user);
        model.addAttribute("myChatRooms", myChatRooms);
        return "chat/index";
    }

    //내 채팅방 목록 조회 메서드
    @GetMapping("/mychat")
    @ResponseBody
    public ResponseEntity<?> getMyChatRoom(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyChatRooms(principal)));
    }

    @GetMapping("/{chatRoomId}")
    public String chatDetailPage(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long chatRoomId, Model model) {
        ChatRoomListResponse myChatRoom = chatService.getChatRoomDetails(user, chatRoomId);
        List<ChatMessageDto> chatHistories = chatService.getChatHistory(user, chatRoomId);
        model.addAttribute("myChatRoom", myChatRoom);
        model.addAttribute("chatHistories", chatHistories);
        model.addAttribute("userEmail", user.getEmail());
        return "chat/detail";
    }

    @PostMapping
    public String createChatRoom(@AuthenticationPrincipal UserPrincipal user, @RequestParam Long listingId, @RequestParam Long sellerId) {
        if (user.getId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "내 판매글 입니다. 채팅방을 생성할 수 없습니다.");
        }
        ChatRoomCreateRequest request = ChatRoomCreateRequest.from(listingId, sellerId, user.getId());
        ChatRoomCreateResponse res = chatService.getOrCreateChatRoom(request);
        return "redirect:/mobile/chat/" + res.getChatRoomId();
    }

    @DeleteMapping("/{chatRoomId}")
    @ResponseBody
    public void leaveChatRoom(@AuthenticationPrincipal UserPrincipal userprincipal, @PathVariable Long chatRoomId){ //채팅방 나가기
        chatService.leaveChatRoom(userprincipal.getId(), chatRoomId);
    }

    @GetMapping("/{chatRoomId}/readAt")
    @ResponseBody
    public void updateReadAt(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserPrincipal user) {
        chatService.updateReadAt(chatRoomId, user.getEmail());
    }

}