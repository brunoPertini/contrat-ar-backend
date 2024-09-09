package com.contractar.microserviciocommons.exceptions.vendibles;

public class OperationNotAllowedException extends RuntimeException {

	private static final long serialVersionUID = 2642444621390331033L;
	
	public OperationNotAllowedException() {
		super("Operacion no permitida");
	}

}
