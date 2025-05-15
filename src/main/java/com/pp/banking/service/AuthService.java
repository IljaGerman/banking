package com.pp.banking.service;

import com.pp.banking.dto.UserDto;
import com.pp.banking.dto.auth.JwtRequest;
import com.pp.banking.dto.auth.JwtResponse;
import com.pp.banking.dto.auth.RefreshTokenRequest;
import com.pp.banking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	public JwtResponse login(JwtRequest loginRequest) {
		if (loginRequest == null) {
			throw new IllegalArgumentException("Login request cannot be null");
		}

		final String loginIdentifier = getLoginIdentifier(loginRequest);
		authenticate(loginIdentifier, loginRequest.getPassword());

		final UserDto user = findUser(loginRequest, loginIdentifier);
		return buildJwtResponse(user);
	}

	public JwtResponse refresh(RefreshTokenRequest refreshToken) {
		return jwtTokenProvider.refreshUserTokens(refreshToken.getRefreshToken());
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(username, password)
		);
	}

	private UserDto findUser(JwtRequest loginRequest, String loginIdentifier) {
		return isLoginByEmail(loginRequest)
			? userService.getUserByEmail(loginIdentifier)
			: userService.getUserByPhone(loginIdentifier);
	}

	private JwtResponse buildJwtResponse(UserDto user) {
		if (user == null) {
			throw new AuthenticationServiceException("User not found");
		}

		return JwtResponse.builder()
			.id(user.getId())
			.username(user.getName())
			.accessToken(jwtTokenProvider.createAccessToken(user.getId()))
			.refreshToken(jwtTokenProvider.createRefreshToken(user.getId()))
			.build();
	}

	private String getLoginIdentifier(JwtRequest loginRequest) {
		if (loginRequest.getPhone() != null && !loginRequest.getPhone().isEmpty()) {
			return loginRequest.getPhone();
		}
		if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
			return loginRequest.getEmail();
		}
		throw new IllegalArgumentException("Either phone or email must be provided");
	}

	private boolean isLoginByEmail(JwtRequest loginRequest) {
		return loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty();
	}

}
