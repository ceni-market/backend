package com.cenimarket.backend.global.security;

import com.cenimarket.backend.auth.oauth.OAuth2SuccessHandler;
import com.cenimarket.backend.auth.service.CustomOAuth2UserService;
import com.cenimarket.backend.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor // JwtTokenProvider 주입을 위해 필요
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MobileAuthenticationEntryPoint mobileAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    /**
     * ⚛️ 1. 리액트 (REST API) 전용 필터 체인
     * /api/로 시작하는 모든 요청은 이 문지기가 검사합니다.
     */
    @Bean
    @Order(1) // 📌 순위 1등으로 설정
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**") // 📌 /api/** 주소만 이 체인이 가로챔
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입 허용
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 📌 리액트 전용 필터 적용 (헤더 검사)
                //.addFilterBefore(new RestJwtFilter(jwtTokenProvider, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 🍃 2. 타임리프 (모바일 SSR 웹뷰) 전용 필터 체인
     * /mobile/ 또는 정적 리소스 요청은 이 문지기가 검사합니다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain mobileSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(mobileAuthenticationEntryPoint)
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 오직 로그인 페이지와 정적 리소스(CSS, 이미지)만 인증 없이 패스!
                        .requestMatchers(
                                "/mobile/login",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 2. [/mobile/main 포함] 나머지 모든 모바일 주소는 로그인(인증) 필수!
                        .requestMatchers("/mobile/**").authenticated()

                        // 3. 전역 잠금
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2SuccessHandler)
                )
                // 📌 [가장 중요 - 수정] UsernamePasswordAuthenticationFilter 대신 LogoutFilter 앞에 둡니다.
                // 이렇게 해야 시큐리티가 예외를 판단하거나 검사하기 전에 우리 커스텀 '쿠키 필터'가 최우선으로 작동합니다.
                .addFilterBefore(new MobileJwtFilter(jwtTokenProvider, refreshTokenService), LogoutFilter.class)
                .build();
    }
}