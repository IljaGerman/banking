package com.pp.banking.security;

import com.pp.banking.dto.UserDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class JwtEntityFactory {

	public static JwtEntity createJwtEntity(UserDto user) {
		return new JwtEntity(
			user.getId(),
			user.getName(),
			user.getName(),
			user.getPassword(),
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

}
