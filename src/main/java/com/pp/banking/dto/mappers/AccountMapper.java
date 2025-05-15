package com.pp.banking.dto.mappers;

import com.pp.banking.dto.AccountDto;
import com.pp.banking.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper extends DefaultMapper<AccountDto, Account> {

	default Account balanceUpdate(AccountDto dto, Account entity) {
		if (dto.getBalance() != null) {
			entity.setBalance(dto.getBalance());
		}
		return entity;
	}

}
