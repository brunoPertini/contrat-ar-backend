package com.contractar.microserviciocommons.exceptions;

public class VendibleAlreadyBindedException extends CustomException {
	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "El producto o servicio ya está vinculado a este proveedor";

	public VendibleAlreadyBindedException() {
		super(message);
		this.STATUS_CODE = 409;
	}
}
