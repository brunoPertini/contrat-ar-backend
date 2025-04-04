package com.contractar.microserviciocommons.exceptions.payment;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class PaymentNotFoundException extends CustomException {

	private static final long serialVersionUID = -2142858115352718591L;

	public PaymentNotFoundException(String message) {
		super(message);
		this.STATUS_CODE = 404;
	}

}
