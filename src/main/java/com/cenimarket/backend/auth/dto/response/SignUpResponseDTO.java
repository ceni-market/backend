package com.cenimarket.backend.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpResponseDTO {

	private Long userId;
	private String email;
	private String emailVerified;
}