package com.cenimarket.backend.global.security;

import com.cenimarket.backend.auth.oauth.OAuth2SuccessHandler;
import com.cenimarket.backend.auth.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor // JwtTokenProvider 주입을 위해 필요
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // 1. 만든 판독기 주입
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final MobileAuthenticationEntryPoint mobileAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(mobileAuthenticationEntryPoint)
                )
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. 공통 허용 경로 (Auth, OAuth2, Swagger)
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/favicon.ico"
                        ).permitAll()
                        // 2.채팅 (채팅은 해당 인증은 허용하고, 별도로 인증 절차 진행)
                        .requestMatchers(
                                "/chat",
                                "/chat/**",
                                "/mobile/chat/**",
                                "/connect/**", //웹소켓 통신 요청, 구독 요청 주소
                                "/topic/**",   //메시지 송신 주소
                                "/publish/**"  //메시지 발행 주소
                        ).permitAll()
                        // 3. 테스트 및 정적 리소스 허용 (중요: uploads 포함)
                        .requestMatchers(
                                "/mobile/**",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico",
                                "/index/**",
                                "/test/**",
                                "/test/listing/**",
                                "/api/uploads/images/**", // 이미지 업로드 경로
                                "/uploads/images/**",     // 이미지 조회 경로
                                "/main/index"
                        ).permitAll()

                        // 모바일 환경 권한 설정
                        .requestMatchers("/mobile/login").permitAll()
                        .requestMatchers("/mobile/**").authenticated()

                        // 3. HTTP 메서드별 권한 제의
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()

                        // 4. [가장 중요] anyRequest는 반드시 마지막에!
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)
                )
                // JWT 필터 추가
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
