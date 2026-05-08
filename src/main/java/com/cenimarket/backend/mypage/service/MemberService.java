package com.cenimarket.backend.mypage.service;

import com.cenimarket.backend.global.error.BusinessException;
import com.cenimarket.backend.global.error.ErrorCode;
import com.cenimarket.backend.mypage.dto.response.MemberResponseDTO;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final UserRepository userRepository;

    public MemberResponseDTO getMyInfoBySecurityContext() {
        // 1. JwtFilter가 저장해둔 신분증(Authentication) 꺼내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 신분증 확인
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        // 3. 신분증에 적힌 이메일로 유저 찾기
        return userRepository.findByEmail(authentication.getName())
                .map(MemberResponseDTO::of)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_ERROR));
    }
}
