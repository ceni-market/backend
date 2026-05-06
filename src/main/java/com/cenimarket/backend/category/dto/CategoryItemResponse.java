package com.cenimarket.backend.category.dto;

import com.cenimarket.backend.category.domain.Category;

public record CategoryItemResponse(
        Long id,
        String name,
        Integer sortOrder
) {

    // Category 엔티티를 카테고리 목록 응답 DTO로 변환한다.
    public static CategoryItemResponse from(Category category) {
        return new CategoryItemResponse(
                category.getId(),
                category.getName(),
                category.getSortOrder()
        );
    }
}
