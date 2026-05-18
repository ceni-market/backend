package com.cenimarket.backend.auth.controller;

import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/mobile")
@RequiredArgsConstructor
//이 코드는 백엔드가 발급한 JWT 토큰을 브라우저의 안전한 보관소(Cookie)에 강제로 저장
public class MobileAuthController {

    private final LoginService loginService;

    // 로그인 페이지 열기 (GET 요청도 이 주소로 처리)
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // src/main/resources/templates/auth/login.html을 엶
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpServletResponse response) {

        // 1. 기존 요청 DTO 객체 생성 후 서비스 호출 (기존 코드 100% 재사용)
        LoginRequestDTO requestDto = new LoginRequestDTO(email, password);
        LoginResponseDTO responseDto = loginService.login(requestDto);

        // 2. AccessToken 쿠키 심기 (1시간 수명)
        Cookie accessCookie = new Cookie("accessToken", responseDto.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60* 24); //개발 편의를 위해 일시적으로 24시간으로 변경
        //accessCookie.setMaxAge(60 * 60); 1시간
        response.addCookie(accessCookie);

        // 3. RefreshToken 쿠키 심기 (30일 장기 수명 부여)
        Cookie refreshCookie = new Cookie("refreshToken", responseDto.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60); // 30일 고정
        response.addCookie(refreshCookie);

        return "redirect:/mobile/main";
    }
}
