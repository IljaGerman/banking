package com.pp.banking.dto;

import com.pp.banking.dto.validation.OnCreate;
import com.pp.banking.dto.validation.OnUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto implements Serializable {

	private Long id;

	@NotNull(
		message = "Name must be not null.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	private String name;
	private LocalDate dateOfBirth;

	@Size(
		min = 8,
		max = 500,
		message = "Password length must be more than 8 symbols and smaller than 500 symbols."
	)
	@NotNull(
		message = "Password must be not null.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	private String password;

	@NotNull(
		message = "Account must be not null.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	private AccountDto account;

	@NotEmpty(message = "At least one email must be provided")
	private Set<EmailDataDto> emails = new HashSet<>();

	@NotEmpty(message = "At least one phone must be provided")
	private Set<PhoneDataDto> phones = new HashSet<>();

}
