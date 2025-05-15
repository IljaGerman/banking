package com.pp.banking.security;

import com.pp.banking.dto.UserDto;
import com.pp.banking.dto.auth.JwtResponse;
import com.pp.banking.exception.AccessDeniedException;
import com.pp.banking.security.properties.JwtProperties;
import com.pp.banking.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtProperties jwtProperties;
	private final UserService userService;
	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
	}

	public String createAccessToken(Long userId) {
		Claims claims = Jwts.claims()
			.add("user_id", userId)
			.build();
		Instant validity = Instant.now()
			.plus(jwtProperties.getAccess(), ChronoUnit.HOURS);
		return Jwts.builder()
			.claims(claims)
			.expiration(Date.from(validity))
			.signWith(key)
			.compact();
	}

	public String createRefreshToken(Long userId) {
		Claims claims = Jwts.claims()
			.add("user_id", userId)
			.build();
		Instant validity = Instant.now()
			.plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);
		return Jwts.builder()
			.claims(claims)
			.expiration(Date.from(validity))
			.signWith(key)
			.compact();
	}

	public JwtResponse refreshUserTokens(String refreshToken) {
		JwtResponse jwtResponse = new JwtResponse();
		if (!isValid(refreshToken)) {
			throw new AccessDeniedException("Refresh token is invalid");
		}
		Long userId = getId(refreshToken);
		UserDto user = userService.getUserById(userId);
		jwtResponse.setId(userId);
		jwtResponse.setUsername(user.getName());
		jwtResponse.setAccessToken(
			createAccessToken(userId)
		);
		jwtResponse.setRefreshToken(
			createRefreshToken(userId)
		);
		return jwtResponse;
	}

	public boolean isValid(String token) {
		Jws<Claims> claims = Jwts
			.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token);
		return claims.getPayload()
			.getExpiration()
			.after(new Date());
	}

	public Long getId(String token) {
		return Jwts
			.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("user_id", Long.class);
	}

	public Authentication getAuthentication(String token) {
		Long id = getId(token);
		UserDetails userDetails = JwtEntityFactory.createJwtEntity(
			userService.getUserById(id)
		);
		return new UsernamePasswordAuthenticationToken(
			userDetails,
			"",
			userDetails.getAuthorities()
		);
	}

}
