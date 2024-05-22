package com.contractar.microserviciocommons.exceptions.vendibles;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class CantCreateException extends CustomException{

	private static final long serialVersionUID = -4002800964405399390L;
	public static final String message = "¡Alguno de los campos es nulo o inválido!";

	public CantCreateException() {
		super(message);
		this.STATUS_CODE = 400;
	}

	public CantCreateException(String message, Throwable err) {
		super(message, err);
		this.STATUS_CODE = 400;
	}

	public CantCreateException(Throwable err) {
		super(message, err);
		this.STATUS_CODE = 400;
	}

}
