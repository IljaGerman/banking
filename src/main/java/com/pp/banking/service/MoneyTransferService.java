package com.pp.banking.service;

import com.pp.banking.dto.AccountDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyTransferService {

	private final UserService userService;
	private final AccountService accountService;

	@Caching(evict = {
		@CacheEvict(value = "UserService::findUsers", allEntries = true),
		@CacheEvict(value = "UserService::getUserById", key = "#fromUserId"),
		@CacheEvict(value = "UserService::getUserById", key = "#toUserId")
	})
	@Transactional
	public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
		log.info("Initiating money transfer: from user {} to user {}, amount {}",
			fromUserId, toUserId, amount);
		validateTransferRequest(fromUserId, toUserId, amount);

		AccountDto fromAccount = accountService.getAccountByUserIdWithLock(fromUserId);
		AccountDto toAccount = accountService.getAccountByUserIdWithLock(toUserId);

		if (fromAccount.getBalance().compareTo(amount) < 0) {
			throw new IllegalArgumentException("Not enough money on account");
		}

		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
		toAccount.setBalance(toAccount.getBalance().add(amount));

		accountService.saveAccount(fromAccount);
		accountService.saveAccount(toAccount);
		log.info("Transfer successful: from {} to {}, amount {}. New balances: from {}={}, to {}={}",
			fromUserId, toUserId, amount,
			fromAccount.getId(), fromAccount.getBalance(),
			toAccount.getId(), toAccount.getBalance());
	}

	private void validateTransferRequest(Long fromUserId, Long toUserId, BigDecimal amount) {
		if (fromUserId.equals(toUserId)) {
			throw new IllegalArgumentException("Cannot transfer money to yourself");
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		if (amount.scale() > 2) {
			throw new IllegalArgumentException("Amount must have no more than 2 decimal places");
		}

		if (!userService.existsById(toUserId)) {
			throw new EntityNotFoundException("Receiver user not found");
		}
	}

}
