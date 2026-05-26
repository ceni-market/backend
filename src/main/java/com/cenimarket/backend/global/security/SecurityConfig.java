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
     * ⚛️ [방 1] 리액트 (REST API + 소셜 로그인) 전용 필터 체인
     * - 리액트에서 들어오는 모든 데이터 요청 및 소셜 로그인을 전담합니다.
     * - OAuth2 인증 도중 세션이 필요하므로 세션 정책을 IF_REQUIRED로 설정합니다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // 소셜 로그인이 리액트 영역이므로 세션 정책을 IF_REQUIRED로 완화하여 유실 방지
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 소셜 로그인 관련 백엔드 엔드포인트가 /api/로 시작할 경우를 대비해 개방
                        .requestMatchers("/api/auth/**", "/api/oauth2/**", "/api/login/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()
                        .requestMatchers("/api/uploads/images/**", "/api/test/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 📌 [이식] 리액트 체인 안으로 소셜 로그인 처리 필터 장착
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2SuccessHandler)
                )
                .addFilterBefore(new RestJwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 🍃 [방 2] 모바일 (타임리프 SSR 웹뷰) 전용 필터 체인
     * - 순수하게 모바일 내부 화면 렌더링용 주소만 관리합니다.
     * - 소셜 로그인 짐을 덜어냈으므로, 마음 편히 완벽한 STATELESS 환경으로 잠금 처리합니다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain mobileSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/mobile/**")
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(mobileAuthenticationEntryPoint)
                )
                .csrf(csrf -> csrf.disable())
                // 📌 [수정] 소셜 로그인이 빠졌기 때문에 모바일은 순수 토큰제(STATELESS)로 안전하게 변경 가능
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/mobile/login").permitAll()
                        .requestMatchers("/mobile/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new MobileJwtFilter(jwtTokenProvider, refreshTokenService), LogoutFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/mobile/logout") // 로그아웃 처리 엔드포인트
                        .logoutSuccessUrl("/mobile/login") // 로그아웃 성공 시 이동할 주소
                        .invalidateHttpSession(true)
                        .deleteCookies("accessToken", "refreshToken", "JSESSIONID") // 💡 현재 사용 중인 토큰 쿠키명 입력
                        .addLogoutHandler((request, response, authentication) -> {
                            // 인증 정보가 있다면 DB에서 리프레시 토큰 무효화(삭제)
                            if (authentication != null && authentication.getName() != null) {
                                String email = authentication.getName();
                                // 💡 RefreshTokenService에 구현된 삭제 메서드 호출
                                refreshTokenService.deleteByEmail(email);
                            }
                        })
                )
                .build();
    }

    /**
     * 🛠️ [방 3] 기타 공통 자원 (Swagger 및 정적 리소스, 소셜 로그인 기본 루프 경로)
     */
    @Bean
    @Order(3)
    public SecurityFilterChain etcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(
                        "/v3/api-docs/**", "/swagger-ui/**", "/oauth2/**", "/login/oauth2/code/**",
                        "/css/**", "/images/**", "/favicon.ico",
                        "/uploads/images/**","/uploads/profiles/**", "/index/**", "/test/**", "/main/index"
                )
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }

    /**
     * 🌐 4. 글로벌 CORS 허용 정책 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://www.ceni-market.site",
                "https://ceni-market.site",
                "https://m.ceni-market.site"

        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}