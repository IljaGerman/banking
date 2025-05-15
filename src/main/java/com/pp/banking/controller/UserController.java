package com.pp.banking.controller;

import com.pp.banking.dto.EmailDataDto;
import com.pp.banking.dto.PageDto;
import com.pp.banking.dto.PhoneDataDto;
import com.pp.banking.dto.UserDto;
import com.pp.banking.dto.validation.OnCreate;
import com.pp.banking.service.EmailService;
import com.pp.banking.service.PhoneService;
import com.pp.banking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final PhoneService phoneService;

	@Operation(summary = "Add email to user")
	@PostMapping("/{id}/emails")
	@PreAuthorize("@cse.canAccessUser(#id)")
	public EmailDataDto addEmail(@PathVariable Long id,
								 @Validated(OnCreate.class) @RequestBody EmailDataDto emailDataDto) {
		return emailService.addEmail(emailDataDto, id);
	}

	@Operation(summary = "Add phone to user")
	@PostMapping("/{id}/phones")
	@PreAuthorize("@cse.canAccessUser(#id)")
	public PhoneDataDto addPhone(@PathVariable Long id,
								 @Validated @RequestBody PhoneDataDto phoneData) {
		return phoneService.addPhone(phoneData, id);
	}

	@Operation(summary = "Get users by parameters")
	@GetMapping
	public PageDto<UserDto> getAllUsers(@RequestParam(value = "dateOfBirth", required = false)
										@DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,
										@RequestParam(value = "phone", required = false) String phone,
										@RequestParam(value = "name", required = false) String name,
										@RequestParam(value = "email", required = false) String email,
										@Parameter(
											description = "Pageable parameters (page, size, sort)",
											example = "{\"page\": 0, \"size\": 10}"
										)
										@PageableDefault Pageable pageable
	) {
		return userService.findUsers(dateOfBirth, phone, name, email, pageable);
	}

}
