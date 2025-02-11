package com.contractar.microserviciocommons.exceptions;

public class SessionExpiredException extends CustomException {

	private static final long serialVersionUID = 7352358569503580738L;

	public SessionExpiredException(String message) {
		super(message);
		this.STATUS_CODE = 401;
	}

}
