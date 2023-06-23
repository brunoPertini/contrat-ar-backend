package com.contractar.microserviciooauth.infra;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	 @ExceptionHandler(value = { UserNotFoundException.class, HttpClientErrorException.class })
	public ResponseEntity<Object> handleException(Exception ex) {
		 if (ex instanceof HttpClientErrorException) {
			 HttpClientErrorException castedException = (HttpClientErrorException) ex;
				return new ExceptionFactory().getResponseException(castedException.getResponseBodyAsString(),
						castedException.getStatusCode());
		 } else {
			 UserNotFoundException castedException = (UserNotFoundException) ex;
			 return new ExceptionFactory().getResponseException(castedException.getMessage(),
						HttpStatusCode.valueOf(castedException.STATUS_CODE));
		 }
		
	}
}
