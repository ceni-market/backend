package com.cenimarket.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@Setter 재설정 불필요, 보안
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshTokenRequestDTO {

	@NotBlank(message = "리프레시 토큰은 필수 입력 값입니다.")
	private String refreshToken;

}