package com.contractar.microserviciocommons.exceptions;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 6456173146490537236L;
	
	public final int STATUS_CODE = 404;
	
	public UserNotFoundException() {
		super("Por favor revise su email o contraseña");
	}
	
	public UserNotFoundException(String message , Throwable err) {
		super(message, err);
	}	

}
