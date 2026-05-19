package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.response.ChatRoomListResponse;
import com.cenimarket.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MobileChatController {

    private final ChatService chatService;

    @GetMapping("/mobile/chat")
    public String chatPage(/*@CookieValue , Model model*/) {
//        List<ChatRoomListResponse> myChatRooms = chatService.getMyChatRoom();
//        model.addAttribute("chatRooms", myChatRooms);
        return "chat/index";
    }

    @GetMapping("/mobile/chat/detail")
    public String chatDetailPage() {
        return "chat/detail";
    }
}
