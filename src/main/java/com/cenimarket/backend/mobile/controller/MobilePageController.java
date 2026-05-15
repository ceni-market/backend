package com.cenimarket.backend.mobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobilePageController {
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}