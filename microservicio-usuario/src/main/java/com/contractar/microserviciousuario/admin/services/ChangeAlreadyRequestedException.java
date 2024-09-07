package com.contractar.microserviciousuario.admin.services;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class ChangeAlreadyRequestedException extends CustomException{

	private static final long serialVersionUID = 8943249161394638153L;

	public ChangeAlreadyRequestedException() {
		super("El cambio ya fue solicitado y está siendo procesado");
		this.STATUS_CODE = 409;
	}

	public ChangeAlreadyRequestedException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
