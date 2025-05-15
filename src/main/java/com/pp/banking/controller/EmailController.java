package com.pp.banking.controller;

import com.pp.banking.dto.EmailDataDto;
import com.pp.banking.dto.validation.OnUpdate;
import com.pp.banking.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

	private final EmailService emailService;

	@Operation(summary = "Update user email")
	@PutMapping
	@PreAuthorize("@cse.canAccessEmail(#emailData.id)")
	public EmailDataDto update(@Validated(OnUpdate.class) @RequestBody EmailDataDto emailData) {
		return emailService.updateEmail(emailData);
	}

	@Operation(summary = "Remove user email")
	@DeleteMapping("/{id}")
	@PreAuthorize("@cse.canAccessEmail(#id)")
	public void deleteById(@PathVariable final Long id) {
		emailService.deleteEmail(id);
	}

}
