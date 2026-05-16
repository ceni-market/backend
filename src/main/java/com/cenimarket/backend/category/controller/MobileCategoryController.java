package com.cenimarket.backend.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileCategoryController {
    @GetMapping("/mobile/category")
    public String categoryPage() {
        return "category/index";
    }
}
