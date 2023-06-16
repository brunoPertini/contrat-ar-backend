package com.contractar.microserviciousuario.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	 @ExceptionHandler(value 
		      = { UserNotFoundException.class })
	public ResponseEntity<Object> handleException(Exception ex) {	 
		 HttpStatus httpStatus = HttpStatus.valueOf(404);
			return ResponseEntity.status(httpStatus)
			  .contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
	}
}
