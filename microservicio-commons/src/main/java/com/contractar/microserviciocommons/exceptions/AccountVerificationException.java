package com.contractar.microserviciocommons.exceptions;

public class AccountVerificationException extends CustomException {
	private static final long serialVersionUID = 3165228597162861870L;
	
	// 400 = token expired; 401 = account already verified
	private  int verificationCode;

	public int getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(int verificationCode) {
		this.verificationCode = verificationCode;
	}

	public AccountVerificationException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}
	
	public AccountVerificationException(String message, int verificationCode) {
		super(message);
		this.STATUS_CODE = 409;
		this.verificationCode = verificationCode;
	}
}
