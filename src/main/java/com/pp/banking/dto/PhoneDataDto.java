package com.pp.banking.dto;

import com.pp.banking.dto.validation.OnCreate;
import com.pp.banking.dto.validation.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
public class PhoneDataDto implements Serializable {

	@NotNull(
		message = "Id must be not null.",
		groups = OnUpdate.class
	)
	private Long id;

	@NotNull(
		message = "Phone must be not null.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	@Length(
		max = 13,
		message = "Phone length must be smaller than 200 symbols.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	private String phone;

}
