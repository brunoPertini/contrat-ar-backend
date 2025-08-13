package com.contractar.microserviciocommons.exceptions;

public class CantCreatePromotion extends CustomException {

	private static final long serialVersionUID = -2908938845934726797L;

	public CantCreatePromotion(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
