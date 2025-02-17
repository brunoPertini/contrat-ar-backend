package com.contractar.microserviciousuario.admin.services;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class ChangeConfirmException extends CustomException{
	private static final long serialVersionUID = -5978740356779024350L;

	public ChangeConfirmException() {
		super("No se pudo confirmar el cambio solicitado");
		this.STATUS_CODE = 409;
	}
	
	public ChangeConfirmException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}
}
