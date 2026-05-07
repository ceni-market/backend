package com.cenimarket.backend.category.dto;

public record CategoryItemResponse(
        Long id,
        String name,
        Integer sortOrder
) {

    // Category 엔티티를 카테고리 목록 응답 DTO로 변환한다.
    public static CategoryItemResponse of(Long id, String name, Integer sortOrder) {
        return new CategoryItemResponse(id, name, sortOrder);
    }
}