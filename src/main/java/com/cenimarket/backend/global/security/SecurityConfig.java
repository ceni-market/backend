package com.cenimarket.backend.global.security;

import com.cenimarket.backend.auth.oauth.OAuth2SuccessHandler;
import com.cenimarket.backend.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor // JwtTokenProvider 주입을 위해 필요
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // 1. 만든 판독기 주입
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // 2. 세션 정책 설정 (JWT를 쓰므로 세션을 서버에 생성하지 않음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 3. 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**").permitAll() // 홈 화면, 로그인, 회원가입 관련은 인증 없이 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll() // 2. ⭐ Swagger 관련 모든 경로 허용 (토큰 없이 접근 가능)
                        // 게시글 조회 API는 토큰 없이 허용
                        .requestMatchers(HttpMethod.GET, "/api/listings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()
                        .requestMatchers(
                                "/chat/**",
                                "/chatroom",
                                "/connect/**",
                                "/publish/**",
                                "/topic/**").permitAll() //채팅관련 url
                        .requestMatchers(
                                "/index.html",
                                "/index/**",
                                "/test/**",
                                "/test/listing/**",
                                "/api/uploads/images",
                                "/uploads/images/**").permitAll() //test용
                        //.requestMatchers("/**").permitAll() // 일단 다되게 만듬
                        .requestMatchers("/oauth2/**").permitAll()
                        .anyRequest().authenticated()               // 그 외 모든 요청은 인증(토큰) 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                // 이제 선언된 필드를 사용하여 에러가 사라집니다.
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)
                )
                // 4. JWT 필터 추가 (UsernamePasswordAuthenticationFilter보다 먼저 실행)
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
