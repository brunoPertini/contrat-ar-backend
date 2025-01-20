package com.contractar.microserviciocommons.exceptions.payment;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class PaymentAlreadyDone extends CustomException{

	private static final long serialVersionUID = -821549956538192772L;

	public PaymentAlreadyDone(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
