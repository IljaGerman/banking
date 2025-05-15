package com.pp.banking.service;

import com.pp.banking.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InitialDepositCache {

	private static final String CACHE_KEY_PREFIX = "account:initial_deposit:";

	private final StringRedisTemplate redisTemplate;

	public void initializeIfNeeded(AccountDto account) {
		Boolean exists = redisTemplate.hasKey(getCacheKey(account.getId()));

		if (!exists) {
			synchronized (this) {
				exists = redisTemplate.hasKey(getCacheKey(account.getId()));
				if (!exists) {
					redisTemplate.opsForValue().set(
						getCacheKey(account.getId()),
						account.getBalance().toString()
					);

					redisTemplate.expire(
						getCacheKey(account.getId()),
						6, TimeUnit.MINUTES
					);
				}
			}
		}
	}

	public BigDecimal getInitialDeposit(Long accountId) {
		String value = redisTemplate.opsForValue().get(getCacheKey(accountId));
		return new BigDecimal(value);
	}

	public void updateInitialDeposit(Long accountId, BigDecimal amount) {
		redisTemplate.opsForValue().set(
			getCacheKey(accountId),
			amount.toString()
		);
	}

	private String getCacheKey(Long accountId) {
		return CACHE_KEY_PREFIX + accountId;
	}

}
