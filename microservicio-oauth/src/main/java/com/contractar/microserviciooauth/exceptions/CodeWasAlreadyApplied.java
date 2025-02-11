package com.contractar.microserviciooauth.exceptions;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class CodeWasAlreadyApplied extends CustomException {

	private static final long serialVersionUID = -276576230542510756L;

	public CodeWasAlreadyApplied(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
