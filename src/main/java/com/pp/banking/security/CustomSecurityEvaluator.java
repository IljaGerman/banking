package com.pp.banking.security;

import com.pp.banking.service.EmailService;
import com.pp.banking.service.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("cse")
@RequiredArgsConstructor
public class CustomSecurityEvaluator {

	private final EmailService emailService;
	private final PhoneService phoneService;

	public boolean canAccessEmail(Long emailId) {
		log.info("Checking access for email ID: {}", emailId);
		JwtEntity user = this.getPrincipal();
		Long userId = user.getId();

		return emailService.isEmailOwner(userId, emailId);
	}

	public boolean canAccessPhone(Long phoneId) {
		log.info("Checking access for phone ID: {}", phoneId);
		JwtEntity user = this.getPrincipal();
		Long userId = user.getId();

		return phoneService.isPhoneOwner(userId, phoneId);
	}

	public boolean canAccessUser(
		final Long id
	) {
		JwtEntity user = getPrincipal();
		Long userId = user.getId();

		return userId.equals(id);
	}

	private JwtEntity getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext()
			.getAuthentication();
		return (JwtEntity) authentication.getPrincipal();
	}

}
