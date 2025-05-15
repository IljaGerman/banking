package com.pp.banking.controller;

import com.pp.banking.dto.auth.JwtRequest;
import com.pp.banking.dto.auth.JwtResponse;
import com.pp.banking.dto.auth.RefreshTokenRequest;
import com.pp.banking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

	private final AuthService authService;

	@Operation(
		summary = "User login",
		description = "Authenticates user and returns JWT tokens")
	@PostMapping("/login")
	public JwtResponse login(
		@Validated @RequestBody JwtRequest loginRequest
	) {
		return authService.login(loginRequest);
	}

	@Operation(
		summary = "Refresh token",
		description = "Generates new access token using refresh token")
	@PostMapping("/refresh")
	public JwtResponse refresh(
		@Validated @RequestBody RefreshTokenRequest refreshToken
	) {
		return authService.refresh(refreshToken);
	}

}
