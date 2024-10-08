package com.contractar.microserviciocommons.exceptions.vendibles;

public class SubscriptionAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -4216177312308988111L;
	
	public SubscriptionAlreadyExistsException(String message) {
		super(message);
	}

}
