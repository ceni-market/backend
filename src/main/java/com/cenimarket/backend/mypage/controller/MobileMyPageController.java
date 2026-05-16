package com.cenimarket.backend.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileMyPageController {
    @GetMapping("/mobile/mypage")
    public String myPage() {
        return "mypage/index";
    }
}
