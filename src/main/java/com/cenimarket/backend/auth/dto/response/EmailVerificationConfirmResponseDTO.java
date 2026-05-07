package com.cenimarket.backend.auth.dto.response;

import com.cenimarket.backend.auth.domain.EmailVerification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerificationConfirmResponseDTO {
	private String email;
	private boolean success;   // 인증 성공 여부
	private String redirectUrl; // (선택) 인증 후 이동할 회원가입 완료 폼 주소

	// 인증 성공 팩토리 메서드
	public static EmailVerificationConfirmResponseDTO success(String email) {
		return EmailVerificationConfirmResponseDTO.builder()
				.email(email)
				.success(true)
				.redirectUrl("/signup")
				.build();
	}

	// 인증 실패 팩토리 메서드
	public static EmailVerificationConfirmResponseDTO fail(String email) {
		return EmailVerificationConfirmResponseDTO.builder()
				.email(email)
				.success(false)
				.redirectUrl("/signup/verify-error")
				.build();
	}
}