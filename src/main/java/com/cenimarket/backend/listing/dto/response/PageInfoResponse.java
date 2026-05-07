package com.cenimarket.backend.listing.dto.response;

import org.springframework.data.domain.Page;

public record PageInfoResponse(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public static PageInfoResponse from(Page<?> page) {
        return new PageInfoResponse(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}