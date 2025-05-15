package com.pp.banking.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

	private Long id;
	private String username;
	private String accessToken;
	private String refreshToken;

}
