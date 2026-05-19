package com.cenimarket.backend.category.controller;

import com.cenimarket.backend.category.dto.CategoryItemResponse;
import com.cenimarket.backend.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MobileCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/mobile/category")
    public String categoryPage(Model model) {
        List<CategoryItemResponse> categories = categoryService.getCategories();

        model.addAttribute("categories", categories);

        return "category/index";
    }
}
