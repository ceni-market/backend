package com.cenimarket.backend.auth.dto.response;

import com.cenimarket.backend.auth.domain.EmailVerification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerificationResponseDTO {

	private long verificationId;
	private LocalDateTime expiresAt;

	public static EmailVerificationResponseDTO from(EmailVerification verification) {
		return EmailVerificationResponseDTO.builder()
				.verificationId(verification.getId()) // 엔티티 PK가 Long인 경우 String 변환
				.expiresAt(verification.getExpiresAt())
				.build();
	}

}