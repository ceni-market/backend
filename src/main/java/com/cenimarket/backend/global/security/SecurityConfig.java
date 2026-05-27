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
     * 🔐 [방 1] 소셜 로그인 핸들링 전용 필터 체인 (신설)
     * - 오직 구글, 카카오 인증 처리 및 리다이렉트만 담당합니다.
     * - 인증 과정의 컨텍스트 유지를 위해 세션 정책을 IF_REQUIRED로 설정합니다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // OAuth2 흐름을 위해 세션 정책 완화 허용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 구글/카카오 창 진입은 전면 허용
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2SuccessHandler) // 성공 시 여기서 JWT 토큰을 만들어 프론트로 토스함
                )
                .build();
    }

    /**
     * ⚛️ [방 2] 리액트 순수 REST API 전용 필터 체인 (수정)
     * - 완벽한 무상태(STATELESS)로 잠금 처리
     * - 프론트엔드의 모든 데이터 통신 요청을 안전하게 받아냅니다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain reactApiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // ⭐ 핵심: 소셜 로그인을 분리했기 때문에 완벽한 STATELESS(무상태) 방어막 구축 가능!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 프론트의 apiClient 401 인터셉터(심폐소생술)가 낚아챌 수 있도록 깔끔하게 401 반환
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 리프레시 전면 개방
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()
                        .requestMatchers("/api/uploads/images/**", "/api/test/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new RestJwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 🍃 [방 3] 모바일 (타임리프 SSR 웹뷰) 전용 필터 체인
     */
    @Bean
    @Order(3)
    public SecurityFilterChain mobileSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/mobile/**")
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(mobileAuthenticationEntryPoint)
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/mobile/login").permitAll()
                        .requestMatchers("/mobile/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new MobileJwtFilter(jwtTokenProvider, refreshTokenService), LogoutFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/mobile/logout")
                        .logoutSuccessUrl("/mobile/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("accessToken", "refreshToken", "JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getName() != null) {
                                String email = authentication.getName();
                                refreshTokenService.deleteByEmail(email);
                            }
                        })
                )
                .build();
    }

    /**
     * 🛠️ [방 4] 기타 공통 자원 (Swagger 및 정적 리소스)
     */
    @Bean
    @Order(4)
    public SecurityFilterChain etcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(
                        "/v3/api-docs/**", "/swagger-ui/**",
                        "/css/**", "/images/**", "/favicon.ico",
                        "/uploads/images/**", "/uploads/profiles/**", "/index/**", "/test/**", "/main/index"
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