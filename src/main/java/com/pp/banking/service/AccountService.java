package com.pp.banking.service;

import com.pp.banking.dto.AccountDto;
import com.pp.banking.dto.mappers.AccountMapper;
import com.pp.banking.dto.mappers.CycleAvoidingMappingContext;
import com.pp.banking.model.Account;
import com.pp.banking.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final AccountMapper accountMapper;
	private final InitialDepositCache initialDepositCache;
	private final CacheManager cacheManager;

	@Transactional
	public AccountDto getAccountByUserIdWithLock(Long userId) {
		Account account = accountRepository.findByUserIdWithLock(userId)
			.orElseThrow(() -> new EntityNotFoundException("Account not found for user: " + userId));

		return accountMapper.toDto(account, new CycleAvoidingMappingContext());
	}

	@Transactional
	public void saveAccount(AccountDto accountDto) {
		Account existingAccount = accountRepository.findById(accountDto.getId())
			.orElseThrow(() ->
				new EntityNotFoundException("Account not found for id: " + accountDto.getId())
			);
		Account updatedAccount = accountMapper.balanceUpdate(accountDto, existingAccount);
		accountRepository.save(updatedAccount);
	}

	@Transactional
	public void increaseBalances() {
		log.info("Starting balance increase process");
		List<Account> accounts = accountRepository.findByBonusCreditedFalse();
		accounts.forEach(this::processAccountBalanceIncrease);
		log.info("Completed balance increase process");
	}

	private void processAccountBalanceIncrease(Account account) {
		ensureInitialDepositCached(account);
		BigDecimal newBalance = calculateNewBalance(account);

		if (shouldUpdateBalance(account, newBalance)) {
			log.debug("Processing account ID: {}", account.getId());
			updateAccountBalance(account, newBalance);
			evictUserCache(account.getUser().getId());
		}
	}

	private void ensureInitialDepositCached(Account account) {
		initialDepositCache.initializeIfNeeded(accountMapper.toDto(
			account, new CycleAvoidingMappingContext()
		));
		if (initialDepositCache.getInitialDeposit(account.getId()) == null) {
			initialDepositCache.updateInitialDeposit(account.getId(), account.getBalance());
		}
	}

	private BigDecimal calculateNewBalance(Account account) {
		BigDecimal currentBalance = account.getBalance();
		BigDecimal initialDeposit = initialDepositCache.getInitialDeposit(account.getId());
		BigDecimal maxAllowedBalance = roundBalance(initialDeposit.multiply(new BigDecimal("2.07")));
		BigDecimal newBalance = roundBalance(currentBalance.multiply(new BigDecimal("1.10")));

		if (newBalance.compareTo(maxAllowedBalance) >= 0) {
			log.info("Account ID {} reached maximum allowed bonus (207%)", account.getId());
			account.setBonusCredited(true);
			return maxAllowedBalance;
		}
		return newBalance;
	}

	private boolean shouldUpdateBalance(Account account, BigDecimal newBalance) {
		return account.getBalance().compareTo(newBalance) != 0;
	}

	private void updateAccountBalance(Account account, BigDecimal newBalance) {
		account.setBalance(newBalance);
		accountRepository.save(account);
	}

	private BigDecimal roundBalance(BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		return amount.setScale(2, RoundingMode.FLOOR);
	}

	private void evictUserCache(Long userId) {
		cacheManager.getCache("UserService::getUserById").evict(userId);
		cacheManager.getCache("UserService::findUsers").clear();
	}

}