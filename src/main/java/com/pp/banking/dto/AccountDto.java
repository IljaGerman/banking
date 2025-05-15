package com.pp.banking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pp.banking.dto.validation.OnCreate;
import com.pp.banking.dto.validation.OnUpdate;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountDto implements Serializable {

	private Long id;

	@DecimalMin(
		value = "0.00",
		message = "Balance must be greater than or equal to 0.",
		groups = {OnCreate.class, OnUpdate.class}
	)
	private BigDecimal balance;

	private boolean bonusCredited;

	@JsonIgnore
	@AssertTrue(message = "Balance must represent rubles and kopecks (exactly 2 decimal places)")
	public boolean isBalanceValid() {
		return balance.scale() <= 2;
	}

}
