package com.contractar.microserviciocommons.exceptions;

public class UserCreationException extends CustomException {
	
	private static final long serialVersionUID = 3647111257507357592L;
	private static final String message = "Error creando el usuario, por favor revise la informacion provista";
	
	public UserCreationException() {
		super(message);
		this.STATUS_CODE = 400;
	}

	public UserCreationException(String message) {
		super(message);
		this.STATUS_CODE = 400;
	}

	public UserCreationException(String message, Throwable err) {
		super(message, err);
	}

}
