package com.pp.banking.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRequest {

	@Email(message = "Email format is invalid")
	private String email;

	private String phone;

	@NotNull(
		message = "Password must be not null."
	)
	private String password;

	@JsonIgnore
	@AssertTrue(message = "Either email or phone must be provided, but not both.")
	public boolean isEitherEmailOrPhone() {
		return (email != null) ^ (phone != null);
	}

}
