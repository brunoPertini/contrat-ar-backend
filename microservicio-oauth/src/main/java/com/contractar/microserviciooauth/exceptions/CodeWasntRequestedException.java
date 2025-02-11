package com.contractar.microserviciooauth.exceptions;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class CodeWasntRequestedException extends CustomException {

	private static final long serialVersionUID = -371372356790777855L;

	public CodeWasntRequestedException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
