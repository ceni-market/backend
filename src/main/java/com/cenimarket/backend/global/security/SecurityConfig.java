package com.cenimarket.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입 관련은 인증 없이 허용
                        // 2. ⭐ Swagger 관련 모든 경로 허용 (토큰 없이 접근 가능)
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/chatroom").permitAll()
                        //.requestMatchers("/**").permitAll() // 일단 다되게 만듬
                        .anyRequest().authenticated()               // 그 외 모든 요청은 인증(토큰) 필요
                )
                // 4. JWT 필터 추가 (UsernamePasswordAuthenticationFilter보다 먼저 실행)
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 솔팅(Salting)과 키 스트레칭을 자동으로 처리하여 password_hash를 안전하게 생성.
        return new BCryptPasswordEncoder();
    }
}
