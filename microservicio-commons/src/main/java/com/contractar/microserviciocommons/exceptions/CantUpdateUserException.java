package com.contractar.microserviciocommons.exceptions;

public class CantUpdateUserException extends CustomException{

	private static final long serialVersionUID = -2747128813017240728L;

	public CantUpdateUserException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
