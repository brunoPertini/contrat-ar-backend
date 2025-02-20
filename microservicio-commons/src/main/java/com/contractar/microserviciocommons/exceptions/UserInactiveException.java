package com.contractar.microserviciocommons.exceptions;

public class UserInactiveException extends CustomException {

	private static final long serialVersionUID = -2806784262923868415L;
	
	private static final String message = "Tu cuenta se encuentra inactiva temporalmente";
	
	private ACCOUNT_STATUS accountStatus;
	
	public UserInactiveException() {
		super(message);
		this.STATUS_CODE = 403;
	}

	public UserInactiveException(String message) {
		super(message);
		this.STATUS_CODE = 403;
	}
	
	public UserInactiveException(String message, ACCOUNT_STATUS status) {
		super(message);
		this.STATUS_CODE = 403;
		this.accountStatus = status;
	}

	public static enum ACCOUNT_STATUS {
		disabled,
		unverified,
	}
	
	public ACCOUNT_STATUS getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(ACCOUNT_STATUS accountStatus) {
		this.accountStatus = accountStatus;
	}

}
