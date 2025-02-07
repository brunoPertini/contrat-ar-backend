package com.contractar.microserviciooauth.exceptions;

public class TwoFaCodeAlreadySendException extends RuntimeException {

	private static final long serialVersionUID = 4995795305484930658L;

	public TwoFaCodeAlreadySendException(String message) {
		super(message);
	}

}
