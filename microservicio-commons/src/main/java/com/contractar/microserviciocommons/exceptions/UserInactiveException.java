package com.contractar.microserviciocommons.exceptions;

public class UserInactiveException extends CustomException {

	private static final long serialVersionUID = -2806784262923868415L;
	
	private static final String message = "Tu cuenta se encuentra inactiva temporalmente";
	
	public UserInactiveException() {
		super(message);
		this.STATUS_CODE = 409;
	}

	public UserInactiveException(String message) {
		super(message);
	}

}
