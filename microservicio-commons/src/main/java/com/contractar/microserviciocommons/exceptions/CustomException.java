package com.contractar.microserviciocommons.exceptions;

public abstract class CustomException extends Exception {
	private static final long serialVersionUID = 5075539949257379L;

	protected int STATUS_CODE = 500;

	protected CustomException(String message) {
		super(message);
	}

	protected CustomException(String message, Throwable err) {
		super(message, err);
	}

	public int getStatusCode() {
		return STATUS_CODE;
	}

}
