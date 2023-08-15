package com.contractar.microserviciousuario.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.VendibleBindingException;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { UserNotFoundException.class })
	public ResponseEntity<Object> handleUserNotFoundException(Exception ex) {
		HttpStatus httpStatus = HttpStatus.valueOf(404);
		return ResponseEntity.status(httpStatus).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
	}

	@ExceptionHandler(value = { VendibleBindingException.class, VendibleAlreadyBindedException.class })
	public ResponseEntity<Object> handleVendibleOperationsExceptions(Exception ex) {
		CustomException castedException = (CustomException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.getStatusCode()));
	}
}
