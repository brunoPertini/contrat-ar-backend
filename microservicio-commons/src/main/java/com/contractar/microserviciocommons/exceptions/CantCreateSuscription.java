package com.contractar.microserviciocommons.exceptions;

public class CantCreateSuscription extends CustomException {

	private static final long serialVersionUID = 613513986072736810L;

	public CantCreateSuscription(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
