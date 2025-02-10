package com.contractar.microserviciooauth.exceptions;

public class TwoFaCodeAlreadySendException extends RuntimeException{

	private static final long serialVersionUID = 4995795305484930658L;

	private int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public TwoFaCodeAlreadySendException(String message) {
		super(message);
		this.statusCode = 409;
	}

}
