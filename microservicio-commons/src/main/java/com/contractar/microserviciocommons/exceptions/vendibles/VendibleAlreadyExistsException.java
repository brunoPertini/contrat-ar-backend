package com.contractar.microserviciocommons.exceptions.vendibles;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class VendibleAlreadyExistsException extends CustomException{

	private static final long serialVersionUID = 8777509554986568767L;

	private static final String message = "El producto o servicio ya existe";

	public VendibleAlreadyExistsException() {
		super(message);
		this.STATUS_CODE = 409;
	}

	public VendibleAlreadyExistsException(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 409;
	}

	public VendibleAlreadyExistsException(Throwable err) {
		super(message, err);
		this.STATUS_CODE = 409;
	}
}
