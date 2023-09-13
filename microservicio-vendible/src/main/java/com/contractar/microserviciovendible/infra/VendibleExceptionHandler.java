package com.contractar.microserviciovendible.infra;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;
import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.infra.RequestsHelper;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice 
public class VendibleExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { HttpClientErrorException.class })
	public ResponseEntity<Object> handleHttpClientExceptions(Exception ex) {
		HttpClientErrorException castedException = (HttpClientErrorException) ex;
		int statusCode = castedException.getStatusCode().value();
		if (statusCode == 404) {
			return new ExceptionFactory().getResponseException("El proveedor no fue encontrado o no corresponde a este vendible", HttpStatus.CONFLICT);
		}
		
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(statusCode));
				
	}
	
	@ExceptionHandler(value = { VendibleNotFoundException.class, VendibleAlreadyExistsException.class })
	public ResponseEntity<Object> handleVendibleOperationsExceptions(Exception ex) {
		CustomException castedException = (CustomException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.getStatusCode()));
	}

	@ExceptionHandler(value = { TransactionSystemException.class })
	public ResponseEntity<Object> handleVendibleUpdateException(TransactionSystemException ex, WebRequest request) {
		Throwable cause = ex.getRootCause();

		if (cause instanceof ConstraintViolationException) {
			try {
				VendibleDTO bodyDTO = RequestsHelper.parseRequestBodyToDTO(request, ServicioDTO.class);
				VendibleUpdateException readableException = new VendibleUpdateException();
				return new ExceptionFactory().getResponseException(readableException.getMessage(),
						HttpStatusCode.valueOf(readableException.getStatusCode()), bodyDTO);
			} catch (IOException | IllegalStateException | NullPointerException e) {
				return new ExceptionFactory().getResponseException("Unknown error", HttpStatusCode.valueOf(500));
			}
		}

		return new ExceptionFactory().getResponseException(ex.getMessage(), HttpStatusCode.valueOf(400));
	}
}