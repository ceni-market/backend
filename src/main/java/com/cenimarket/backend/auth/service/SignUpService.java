package com.cenimarket.backend.auth.service;


import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.auth.dto.request.SignUpRequestDTO;
import com.cenimarket.backend.auth.dto.response.SignUpResponseDTO;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 암호화용
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //보안 알고리즘의 유연한 변경,관심사 분리,테스트 용이성

    /**
     * 회원가입 비즈니스 로직
     */
    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {

        // 1. 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 2. 비밀번호 조건 및 일치 여부 검증
        validatePassword(request.getPassword(), request.getPasswordConfirm());

        // 3. 비밀번호 암호화 및 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 ->스태틱 팩토리 메서드. 의도의 명확성. 캡슐화. 유지보수 용이. 안정성
        User user = User.createNewUser(
                request.getName(),
                request.getEmail(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);

        // 4. 응답 DTO 반환 (변수명 userId, email, emailVerified 유지)
        return SignUpResponseDTO.from(savedUser);
    }

    /*비밀번호 유효성 검사 로직*/
    private void validatePassword(String password, String passwordConfirm) {
        // 클라이언트에서 1차 확인이 되었는지 체크 (DTO 변수 활용)
        if (password == null || !password.equals(passwordConfirm)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 비밀번호 강도 조건 (예: 8자 이상, 영문/숫자 조합 등)
        if (password == null || password.length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

    }
}