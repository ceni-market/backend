package com.cenimarket.backend.auth.dto.response;

import com.cenimarket.backend.user.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginResponseDTO {
	private String accessToken;
	private String refreshToken;
	private Long accessTokenExpiresIn;

	public static LoginResponseDTO of(String accessToken, String refreshToken, Long accessTokenExpiresIn) {
		return LoginResponseDTO.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accessTokenExpiresIn(accessTokenExpiresIn)
				.build();
	}
}