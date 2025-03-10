package com.contractar.microserviciocommons.exceptions;

public class ResetPasswordAlreadyRequested extends CustomException {

	private static final long serialVersionUID = 4639543617090177350L;

	public ResetPasswordAlreadyRequested(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
