package com.contractar.microserviciocommons.infra;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ExceptionFactory {
	private ErrorDetails errorDetails;
	private HttpStatus httpStatus;

	private void prepareResponseObject(String message, HttpStatusCode statusCode, Object relatedFields) {
		this.errorDetails = new ErrorDetails(new Date(), statusCode.value(), message, relatedFields);
		this.httpStatus = HttpStatus.valueOf(statusCode.value());
	}

	public ResponseEntity<Object> getResponseException(String message, HttpStatusCode statusCode) {
		this.prepareResponseObject(message, statusCode, null);
		return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(errorDetails);
	}

	public ResponseEntity<Object> getResponseException(String message, HttpStatusCode statusCode,
			Object relatedFields) {
		this.prepareResponseObject(message, statusCode, relatedFields);
		return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(errorDetails);
	}
}
