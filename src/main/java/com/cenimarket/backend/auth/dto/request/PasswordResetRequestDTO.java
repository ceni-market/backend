package com.cenimarket.backend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@Setter 재설정 불필요, 보안
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordResetRequestDTO {

	@Email(message = "유효한 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank
	private String token;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
			message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	private String newPassword;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
			message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	private String newPasswordConfirm;
}