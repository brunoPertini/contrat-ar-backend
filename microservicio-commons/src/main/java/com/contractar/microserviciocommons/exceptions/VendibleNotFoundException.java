package com.contractar.microserviciocommons.exceptions;

public class VendibleNotFoundException extends Exception{
private static final long serialVersionUID = 6456173146490537236L;
	
	public final int STATUS_CODE = 404;
	
	public VendibleNotFoundException() {
		super("Servicio o producto no encontrado");
	}
	
	public VendibleNotFoundException(String message , Throwable err) {
		super(message, err);
	}	
}
