package com.cenimarket.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
//@Setter 재설정 불필요, 보안
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpRequestDTO {

	@NotBlank
	@Size(max = 10, message = "이름은 10자 이내로 입력해주세요.")
	private String name;
	private String email;
	private String password;
	private String passwordConfirm;
}