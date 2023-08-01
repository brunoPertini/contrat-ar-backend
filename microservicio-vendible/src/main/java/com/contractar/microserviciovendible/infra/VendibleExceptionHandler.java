package com.contractar.microserviciovendible.infra;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;

@ControllerAdvice
public class VendibleExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { VendibleNotFoundException.class })
	public ResponseEntity<Object> handleUserNotFoundException(Exception ex) {
		VendibleNotFoundException castedException = (VendibleNotFoundException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.STATUS_CODE));
	}
}
