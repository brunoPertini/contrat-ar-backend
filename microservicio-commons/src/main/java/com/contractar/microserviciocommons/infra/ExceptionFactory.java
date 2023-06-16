package com.contractar.microserviciocommons.infra;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ExceptionFactory {

	public ResponseEntity<Object> getResponseException(String message, HttpStatusCode statusCode) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), statusCode.value(), message);
		HttpStatus httpStatus = HttpStatus.valueOf(statusCode.value());
		return ResponseEntity.status(httpStatus)
		  .contentType(MediaType.APPLICATION_JSON).body(errorDetails);
	}
}
