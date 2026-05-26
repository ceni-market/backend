package com.cenimarket.backend.user.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
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

@Controller // 💡 HTML 뷰 및 리다이렉트를 위해 @Controller 사용
@RequestMapping("/mobile/user")
@RequiredArgsConstructor
public class MobileWithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/withdraw")
    public String withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute WithdrawalRequestDTO request, // 💡 JSON이 아닌 HTML Form 데이터를 받으므로 @ModelAttribute 사용
            HttpServletResponse response) {

        // 1. 서비스 레이어 호출 (비밀번호 검증, 상태 DELETED 변경, 리프레시 토큰 제거)
        withdrawalService.withdraw(userPrincipal.getEmail(), request.getPassword());

        // 2. 유저 브라우저에 남아있는 인증 쿠키(Access, Refresh, 세션) 즉시 소멸 처리
        clearAuthCookies(response);

        // 3. 탈퇴 처리가 완료되면 로그인 페이지로 리다이렉트 (파라미터로 탈퇴 완료 메시지 전달 가능)
        return "redirect:/mobile/login?withdrawn=true";
    }

    /**
     * 클라이언트 브라우저의 인증 쿠키를 전면 삭제하는 헬퍼 메서드
     */
    private void clearAuthCookies(HttpServletResponse response) {
        String[] cookiesToClear = {"accessToken", "refreshToken", "JSESSIONID"};
        for (String cookieName : cookiesToClear) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 수명을 0으로 설정하여 브라우저가 즉시 삭제하도록 유도
            response.addCookie(cookie);
        }
    }
}