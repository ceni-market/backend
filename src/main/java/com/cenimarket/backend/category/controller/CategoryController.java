package com.cenimarket.backend.category.controller;

import com.cenimarket.backend.category.dto.CategoryItemResponse;
import com.cenimarket.backend.category.service.CategoryService;
import com.cenimarket.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getCategories() {
        // 카테고리 조회 비즈니스 로직은 Service에 위임한다.
        List<CategoryItemResponse> categories = categoryService.getCategories();

        // 프로젝트 공통 응답 형식으로 감싸서 반환한다.
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
