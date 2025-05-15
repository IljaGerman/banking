package com.pp.banking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotUniqueException extends ApiException {

	public NotUniqueException(String message) {
		super(message, HttpStatus.CONFLICT.value());
	}

}
