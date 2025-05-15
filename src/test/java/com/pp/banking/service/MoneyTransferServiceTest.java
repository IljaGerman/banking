package com.pp.banking.service;

import com.pp.banking.dto.AccountDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MoneyTransferServiceTest {

	private MoneyTransferService moneyTransferService;
	private UserService userService;
	private AccountService accountService;

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		accountService = mock(AccountService.class);
		moneyTransferService = new MoneyTransferService(userService, accountService);
	}

	@Test
	void transferMoney_successful() {
		Long fromUserId = 1L;
		Long toUserId = 2L;
		BigDecimal amount = new BigDecimal("100.00");

		AccountDto fromAccount = new AccountDto();
		fromAccount.setId(101L);
		fromAccount.setBalance(new BigDecimal("500.00"));

		AccountDto toAccount = new AccountDto();
		toAccount.setId(202L);
		toAccount.setBalance(new BigDecimal("200.00"));

		when(userService.existsById(toUserId)).thenReturn(true);
		when(accountService.getAccountByUserIdWithLock(fromUserId)).thenReturn(fromAccount);
		when(accountService.getAccountByUserIdWithLock(toUserId)).thenReturn(toAccount);

		moneyTransferService.transferMoney(fromUserId, toUserId, amount);

		assertEquals(new BigDecimal("400.00"), fromAccount.getBalance());
		assertEquals(new BigDecimal("300.00"), toAccount.getBalance());

		verify(accountService).saveAccount(fromAccount);
		verify(accountService).saveAccount(toAccount);
	}

	@Test
	void transferMoney_selfTransfer_throwsException() {
		Long userId = 1L;
		BigDecimal amount = new BigDecimal("100.00");

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> moneyTransferService.transferMoney(userId, userId, amount));

		assertEquals("Cannot transfer money to yourself", exception.getMessage());
	}

	@Test
	void transferMoney_negativeAmount_throwsException() {
		Long fromUserId = 1L;
		Long toUserId = 2L;
		BigDecimal amount = new BigDecimal("-100.00");

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> moneyTransferService.transferMoney(fromUserId, toUserId, amount));

		assertEquals("Amount must be positive", exception.getMessage());
	}

	@Test
	void transferMoney_tooManyDecimalPlaces_throwsException() {
		Long fromUserId = 1L;
		Long toUserId = 2L;
		BigDecimal amount = new BigDecimal("100.123");

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> moneyTransferService.transferMoney(fromUserId, toUserId, amount));

		assertEquals("Amount must have no more than 2 decimal places", exception.getMessage());
	}

	@Test
	void transferMoney_userNotFound_throwsException() {
		Long fromUserId = 1L;
		Long toUserId = 2L;
		BigDecimal amount = new BigDecimal("50.00");

		when(userService.existsById(toUserId)).thenReturn(false);

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> moneyTransferService.transferMoney(fromUserId, toUserId, amount));

		assertEquals("Receiver user not found", exception.getMessage());
	}

	@Test
	void transferMoney_notEnoughFunds_throwsException() {
		Long fromUserId = 1L;
		Long toUserId = 2L;
		BigDecimal amount = new BigDecimal("300.00");

		AccountDto fromAccount = new AccountDto();
		fromAccount.setId(1L);
		fromAccount.setBalance(new BigDecimal("200.00"));

		AccountDto toAccount = new AccountDto();
		toAccount.setId(2L);
		toAccount.setBalance(new BigDecimal("500.00"));

		when(userService.existsById(toUserId)).thenReturn(true);
		when(accountService.getAccountByUserIdWithLock(fromUserId)).thenReturn(fromAccount);
		when(accountService.getAccountByUserIdWithLock(toUserId)).thenReturn(toAccount);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> moneyTransferService.transferMoney(fromUserId, toUserId, amount));

		assertEquals("Not enough money on account", exception.getMessage());
		verify(accountService, never()).saveAccount(any());
	}

}
