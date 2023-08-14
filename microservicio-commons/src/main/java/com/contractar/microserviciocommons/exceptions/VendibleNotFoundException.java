package com.contractar.microserviciocommons.exceptions;

public class VendibleNotFoundException extends CustomException{

	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "Producto o servicio no encontrado";

	public VendibleNotFoundException() {
		super(message);
		this.STATUS_CODE = 404;
	}

	public VendibleNotFoundException(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 404;
	}

	public VendibleNotFoundException(Throwable err) {
		super(message, err);
		this.STATUS_CODE = 404;
	}
}
