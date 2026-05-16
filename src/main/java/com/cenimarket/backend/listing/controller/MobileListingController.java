package com.cenimarket.backend.listing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileListingController {
    @GetMapping("/mobile/main")
    public String loginPage() {
        return "main/index";
    }
}
