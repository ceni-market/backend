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
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위해 주입

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        /* [추가]: 구글에서 사용자 이름 가져오기 */
        String name = (String) attributes.get("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        // DB에서 이메일로 기존 유저 확인 (자동 계정 통합)
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 정책: 탈퇴 유저(DELETED)는 재가입 불가
            if (user.getStatus() == UserStatus.DELETED) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR);
            }
            // 미인증 유저 자동 인증 처리
            if (user.getEmailVerifiedAt() == null) {
                user.setEmailVerifiedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        } else {
            // 2. 신규 가입 처리 (기존 3단계 데이터 규격에 맞춤)
            user = userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString())) // 소셜용 임의 비번
                    .status(UserStatus.ACTIVE)
                    .emailVerifiedAt(LocalDateTime.now()) // 구글이 인증했으므로 바로 인증 처리
                    .build());
        }

        // 3. 인증된 유저 객체 반환 (세션/JWT 생성용)
        return new UserPrincipal(user, attributes);
    }
}
