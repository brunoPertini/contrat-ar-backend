package com.contractar.microservicioimagenes.exceptions;

import com.contractar.microserviciocommons.exceptions.CustomException;

public class ImageUploadException extends CustomException{

	private static final long serialVersionUID = 6367057107266134316L;
	
	private static final String DEFAULT_MESSAGE = "Por favor revisá que tu imagen tenga formato jpg o png, y tenga un tamaño mínimo de  600x400 píxeles";																																																																				
	
	public ImageUploadException(String message) {
		super(DEFAULT_MESSAGE);
		this.STATUS_CODE = 409;
	}

}
