package com.pp.banking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends ApiException {

	public AccessDeniedException(String message) {
		super(message, HttpStatus.FORBIDDEN.value());
	}

}
