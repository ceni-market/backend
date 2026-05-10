package com.cenimarket.backend.auth.dto.response;

import com.cenimarket.backend.user.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpResponseDTO {

	private Long userId;

	public static SignUpResponseDTO from(User user) {
		return SignUpResponseDTO.builder()
				.userId(user.getId())
				.build();
	}
}