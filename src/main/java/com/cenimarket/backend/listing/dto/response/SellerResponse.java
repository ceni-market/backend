package com.cenimarket.backend.listing.dto.response;

import com.cenimarket.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class SellerResponse {
    private final Long id;
    private final String name;
    private final String email;

    public static SellerResponse from(User user) {
        return SellerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}