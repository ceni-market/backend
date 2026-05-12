package com.cenimarket.backend.auth.service;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.domain.UserStatus;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        System.out.println("--- OAuth2 로드 시작 (" + registrationId + ") ---");

        String email = "";
        String name = "";

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (profile != null) ? (String) profile.get("nickname") : "카카오유저";

            System.out.println("카카오에서 가져온 정보: " + email + " / " + name);
        } else {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        // ⭐ 여기가 핵심: 에러가 나면 콘솔에 빨갛게 찍히도록 try-catch 추가
        try {
            String finalEmail = email;
            String finalName = name;

            User user = userRepository.findByEmail(email)
                    .map(existingUser -> {
                        System.out.println("기존 유저 발견: " + existingUser.getEmail());
                        return existingUser;
                    })
                    .orElseGet(() -> {
                        System.out.println("신규 유저 가입 시도: " + finalEmail);
                        // 비밀번호는 소셜 로그인용 랜덤값 혹은 고정값
                        return userRepository.save(User.createSocialUser(finalEmail, finalName, "SOCIAL_AUTH"));
                    });

            System.out.println("--- OAuth2 로드 완료 (성공) ---");
            return new UserPrincipal(user, attributes);

        } catch (Exception e) {
            System.err.println("!!! DB 저장 중 에러 발생 !!!");
            e.printStackTrace(); // 🔴 에러 원인이 콘솔에 상세히 찍힙니다.
            throw new OAuth2AuthenticationException("데이터베이스 처리 중 에러: " + e.getMessage());
        }
    }
}