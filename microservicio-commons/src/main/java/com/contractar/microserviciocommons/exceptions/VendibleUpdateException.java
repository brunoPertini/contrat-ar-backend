package com.contractar.microserviciocommons.exceptions;

public class VendibleUpdateException extends CustomException{

	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "Error tratando de actualizar el producto o servicio";

	public VendibleUpdateException() {
		super(message);
		this.STATUS_CODE = 400;
	}

	public VendibleUpdateException(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 400;
	}

	public VendibleUpdateException(Throwable err) {
		super(message, err);
		this.STATUS_CODE = 400;
	}
}
