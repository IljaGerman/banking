package com.pp.banking.config;

import com.pp.banking.security.JwtTokenFilter;
import com.pp.banking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApplicationConfig {

	private final JwtTokenProvider tokenProvider;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@SneakyThrows
	public AuthenticationManager authenticationManager(
		final AuthenticationConfiguration configuration
	) {
		return configuration.getAuthenticationManager();
	}

	@Bean
	@SneakyThrows
	public SecurityFilterChain filterChain(
		final HttpSecurity httpSecurity
	) {
		httpSecurity
			.csrf(AbstractHttpConfigurer::disable)
			.cors(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(
					SessionCreationPolicy.STATELESS
				)
			)
			.exceptionHandling(auth ->
				auth.authenticationEntryPoint(
						(request, response, exception) -> {
							response.setStatus(HttpStatus.UNAUTHORIZED.value()
							);
							response.getWriter()
								.write("Unauthorized.");
						}
					)
					.accessDeniedHandler(
						(request, response, exception) -> {
							response.setStatus(HttpStatus.FORBIDDEN.value());
							response.getWriter()
								.write("Unauthorized.");
						})
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/auth/**",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/error")
				.permitAll()
				.anyRequest().authenticated())
			.anonymous(AbstractHttpConfigurer::disable)
			.addFilterBefore(new JwtTokenFilter(tokenProvider),
				UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

}
