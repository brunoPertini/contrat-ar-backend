package com.contractar.microserviciocommons.exceptions;

public class VendibleBindingException extends CustomException {

	private static final long serialVersionUID = 5477569417641734158L;
	
	private static final String message = "El proveedor no fue encontrado o no corresponde a este vendible";
	
	public VendibleBindingException() {
		super(message);
		this.STATUS_CODE = 409;
	}

	public VendibleBindingException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}
}
