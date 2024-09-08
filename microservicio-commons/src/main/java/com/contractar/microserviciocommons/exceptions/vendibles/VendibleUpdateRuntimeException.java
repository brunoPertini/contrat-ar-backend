package com.contractar.microserviciocommons.exceptions.vendibles;

public class VendibleUpdateRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -5152253435908708980L;
	
	private static final String message = "Error tratando de actualizar el producto o servicio";
	
	public VendibleUpdateRuntimeException() {
		super(message);
	}

}
