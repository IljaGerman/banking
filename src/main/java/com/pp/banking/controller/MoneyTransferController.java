package com.pp.banking.controller;

import com.pp.banking.security.JwtTokenProvider;
import com.pp.banking.service.MoneyTransferService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class MoneyTransferController {

	private final MoneyTransferService moneyTransferService;
	private final JwtTokenProvider jwtTokenProvider;

	@Operation(summary = "Transfer money to another user")
	@PostMapping("/{userToId}")
	public void transferMoney(@RequestHeader("Authorization") String authHeader,
							  @PathVariable Long userToId,
							  @RequestParam BigDecimal amount) {
		moneyTransferService.transferMoney(
			jwtTokenProvider.getId(authHeader.substring(7)),
			userToId,
			amount
		);
	}

}
