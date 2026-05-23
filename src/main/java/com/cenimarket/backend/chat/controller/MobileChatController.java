package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.chat.dto.response.ChatRoomListResponse;
import com.cenimarket.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mobile/chat")
public class MobileChatController {

    private final ChatService chatService;

    @GetMapping()
    public String chatPage(@AuthenticationPrincipal UserPrincipal principal , Model model) {
        List<ChatRoomListResponse> myChatRooms = chatService.getMyChatRoom(principal);
        model.addAttribute("myChatRooms", myChatRooms);
        return "chat/index";
    }

    @GetMapping("/detail")
    public String chatDetailPage() {
        return "chat/detail";
    }
}
