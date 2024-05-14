package com.contractar.microserviciocommons.exceptions;

public class ImageNotUploadedException extends CustomException {

	private static final long serialVersionUID = -8114875354524488108L;
	
	private static final String DEFAULT_MESSAGE = "La imagen provista no fue encontrada";
	
	public ImageNotUploadedException() {
		super(DEFAULT_MESSAGE);
		this.STATUS_CODE = 409;
	}

	public ImageNotUploadedException(String message) {
		super(message);
		this.STATUS_CODE = 409;
	}

}
