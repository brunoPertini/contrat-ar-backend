package com.contractar.microserviciocommons.exceptions;

public class AccountVerificationException extends CustomException {
	private static final long serialVersionUID = 3165228597162861870L;

	public AccountVerificationException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}
}
