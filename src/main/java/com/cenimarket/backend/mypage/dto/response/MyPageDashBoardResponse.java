package com.cenimarket.backend.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyPageDashBoardResponse {
    private long myListingCount;
    private long likedListingCount;
    private long tradeHistoryCount;
    private long donationHistoryCount;
}
