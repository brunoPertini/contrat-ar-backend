package com.contractar.microserviciocommons.exceptions;

public class VendibleUpdateException extends Exception {

	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "Error tratando de actualizar el producto o servicio";


	public final int STATUS_CODE = 400;

	public VendibleUpdateException() {
		super(message);
	}

	public VendibleUpdateException(String message, Throwable err) {
		super(message, err);
	}

	public VendibleUpdateException(Throwable err) {
		super(message, err);
	}
}
