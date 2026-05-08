package com.cenimarket.backend.auth.dto.response;

import com.cenimarket.backend.user.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor
public class PasswordResetResponseDTO {
	private boolean success;
	private String message;
	private String email;

	public static PasswordResetResponseDTO success(String email) {
		return new PasswordResetResponseDTO(true, "비밀번호가 성공적으로 변경되었습니다.", email);
	}
}