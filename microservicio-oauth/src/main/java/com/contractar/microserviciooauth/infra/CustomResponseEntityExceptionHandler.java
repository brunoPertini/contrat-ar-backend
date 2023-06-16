package com.contractar.microserviciooauth.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	 @ExceptionHandler(value 
		      = { HttpClientErrorException.class, UserNotFoundException.class })
	public ResponseEntity<Object> handleException(HttpClientErrorException ex) {
		 
		return new ExceptionFactory().getResponseException(ex.getResponseBodyAsString(), ex.getStatusCode());
	}
}
