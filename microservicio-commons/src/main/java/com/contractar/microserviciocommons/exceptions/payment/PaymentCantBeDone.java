package com.contractar.microserviciocommons.exceptions.payment;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class PaymentCantBeDone extends CustomException {

	private static final long serialVersionUID = 9008432523356169726L;

	public PaymentCantBeDone(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
