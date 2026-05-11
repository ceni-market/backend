package com.cenimarket.backend.mypage.dto.response;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MyPageInfoResponse {
    private final String profileImageUrl;
    private final String name;

    public static MyPageInfoResponse from(UserPrincipal user) {
        return MyPageInfoResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .name(user.getName())
                .build();
    }
}
