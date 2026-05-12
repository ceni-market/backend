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

                // 1. JWT를 사용하므로 다시 STATELESS로 변경합니다.
                // 성공 핸들러에서 JWT를 발급하고 나면 세션은 필요 없습니다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // 인증 제외 경로 (한데 모아서 관리하는 것이 가독성에 좋습니다)
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/chat/**",
                                "/chatroom",
                                "/connect/**",
                                "/publish/**",
                                "/topic/**",
                                "/index.html",
                                "/favicon.ico" // 아까 떴던 경고 방지
                        ).permitAll()

                        // HTTP 메서드별 권한 제어
                        .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
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
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)

                )

                // JWT 필터 위치 지정
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
