package com.contractar.microserviciocommons.exceptions.proveedores;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class InvalidSuscription extends CustomException {

	private static final long serialVersionUID = 5542544317921920425L;

	protected InvalidSuscription(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
