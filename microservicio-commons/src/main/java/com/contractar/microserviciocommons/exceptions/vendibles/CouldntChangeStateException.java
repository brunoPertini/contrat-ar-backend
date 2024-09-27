package com.contractar.microserviciocommons.exceptions.vendibles;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class CouldntChangeStateException extends CustomException{

	private static final long serialVersionUID = 2123015608760399485L;

	public CouldntChangeStateException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
