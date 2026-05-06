package com.cenimarket.backend.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId,
        Integer sortOrder
){
}
