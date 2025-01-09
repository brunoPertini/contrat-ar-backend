package com.contractar.microserviciocommons.exceptions.proveedores;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class SuscriptionNotFound extends CustomException {

	private static final long serialVersionUID = -8986999502534798676L;

	public SuscriptionNotFound(String message) {
		super(message);
		this.STATUS_CODE = 404;
	}

	public SuscriptionNotFound(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 404;
	}

}
