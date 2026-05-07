package com.cenimarket.backend.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshTokenResponseDTO {
	private String accessToken;
	private String refreshToken;
	private Long accessTokenExpiresIn; // 액세스 토큰 만료 시간 (ms 단위)

	/**
	 * 정적 팩토리 메서드
	 */
	public static LoginResponseDTO of(String accessToken, String refreshToken, Long expiresIn) {
		return LoginResponseDTO.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accessTokenExpiresIn(expiresIn)
				.build();
	}
}