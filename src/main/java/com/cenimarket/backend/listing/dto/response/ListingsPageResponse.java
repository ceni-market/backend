package com.cenimarket.backend.listing.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ListingsPageResponse(
        List<ListingsListResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static ListingsPageResponse from(Page<ListingsListResponse> page) {
        return new ListingsPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
