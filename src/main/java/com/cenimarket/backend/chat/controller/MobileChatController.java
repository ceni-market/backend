package com.cenimarket.backend.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileChatController {
    @GetMapping("/mobile/chat")
    public String chatPage() {
        return "chat/index";
    }

    @GetMapping("/mobile/chat/detail")
    public String chatDetailPage() {
        return "chat/detail";
    }
}
