package com.pp.banking.scheduler;

import com.pp.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceIncreaseScheduler {

	private final AccountService accountService;

	@Scheduled(fixedRate = 30000)
	public void increaseBalancesTask() {
		accountService.increaseBalances();
	}

}