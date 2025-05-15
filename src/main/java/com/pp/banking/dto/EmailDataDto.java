package com.pp.banking.dto;

import com.pp.banking.dto.validation.OnCreate;
import com.pp.banking.dto.validation.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
public class EmailDataDto implements Serializable {

	@NotNull(
		message = "Id must be not null.",
		groups = OnUpdate.class
	)
	private Long id;

	@NotNull(
		message = "Email must be not null.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	@Length(
		max = 200,
		message = "Email length must be smaller than 200 symbols.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	@Email(message = "Email format is invalid",
		groups = {OnCreate.class, OnUpdate.class})
	private String email;

}
