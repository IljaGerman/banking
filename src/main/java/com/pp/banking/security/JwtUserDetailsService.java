package com.pp.banking.security;

import com.pp.banking.dto.UserDto;
import com.pp.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(
		final String username
	) {
		UserDto user;
		if (isEmail(username)) {
			user = userService.getUserByEmail(username);
		} else {
			user = userService.getUserByPhone(username);
		}
		return JwtEntityFactory.createJwtEntity(user);
	}

	private boolean isEmail(String username) {
		return username.contains("@");
	}

}
