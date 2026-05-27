package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.user.dto.request.WithdrawalRequestDTO;
import com.cenimarket.backend.user.service.WithdrawalService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MobileWithdrawalController {

    private final WithdrawalService withdrawalService;

    // 💡 HTML의 th:action="@{/mobile/withdraw}" 주소와 일치시킵니다.
    @PostMapping("/mobile/withdraw")
    public String withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute WithdrawalRequestDTO request,
            HttpServletResponse response) {

        try {
            // 1. 서비스 레이어 호출 (비밀번호 검증 및 탈퇴 처리)
            withdrawalService.withdraw(userPrincipal.getEmail(), request.getPassword());

            // 2. 인증 쿠키 소멸 처리
            clearAuthCookies(response);

            // 3. 성공 시 로그인 페이지로 이동
            return "redirect:/mobile/login?withdrawn=true";

        } catch (BusinessException e) {
            // 💡 실패 시 세션을 전혀 쓰지 않고, 알려주신 마이페이지 주소 뒤에 파라미터를 붙여 리다이렉트합니다.
            return "redirect:/mobile/mypage?error=invalid_password";
        }
    }

    /**
     * 클라이언트 브라우저의 인증 쿠키를 전면 삭제하는 헬퍼 메서드
     */
    private void clearAuthCookies(HttpServletResponse response) {
        String[] cookiesToClear = {"accessToken", "refreshToken", "JSESSIONID"};
        for (String cookieName : cookiesToClear) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}