package com.contractar.microserviciocommons.exceptions;

public class UserNotFoundException extends CustomException{

	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "Por favor revise su email o contraseña";

	public UserNotFoundException() {
		super(message);
		this.STATUS_CODE = 404;
	}
	
	public UserNotFoundException(String message) {
		super(message);
		this.STATUS_CODE = 404;
	}

	public UserNotFoundException(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 404;
	}

	public UserNotFoundException(Throwable err) {
		super(message, err);
		this.STATUS_CODE = 404;
	}
}
