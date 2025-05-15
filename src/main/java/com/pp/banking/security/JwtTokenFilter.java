package com.pp.banking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(
		final ServletRequest servletRequest,
		final ServletResponse servletResponse,
		final FilterChain filterChain
	) {
		String bearerToken = ((HttpServletRequest) servletRequest)
			.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			bearerToken = bearerToken.substring(7);
		}
		try {
			if (bearerToken != null
				&& jwtTokenProvider.isValid(bearerToken)) {
				Authentication authentication
					= jwtTokenProvider.getAuthentication(bearerToken);
				if (authentication != null) {
					SecurityContextHolder.getContext()
						.setAuthentication(authentication);
				}
			}
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (Exception ignored) {
		}
	}

}
