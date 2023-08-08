package com.contractar.microserviciovendible.infra;

import java.io.IOException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.dto.EntityDTO;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.infra.RequestsHelper;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class VendibleExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { VendibleNotFoundException.class })
	public ResponseEntity<Object> handleVendibleNotFoundException(Exception ex) {
		VendibleNotFoundException castedException = (VendibleNotFoundException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.STATUS_CODE));
	}

	@ExceptionHandler(value = { TransactionSystemException.class })
	public ResponseEntity<Object> handleVendibleUpdateException(TransactionSystemException ex, WebRequest request) {
		Throwable cause = ex.getRootCause();

		if (cause instanceof ConstraintViolationException) {
			try {
				EntityDTO bodyDTO = RequestsHelper.parseRequestBodyToDTO(request, ServicioDTO.class);
				VendibleUpdateException readableException = new VendibleUpdateException();
				return new ExceptionFactory().getResponseException(readableException.getMessage(),
						HttpStatusCode.valueOf(readableException.STATUS_CODE), bodyDTO);
			} catch (IOException | IllegalStateException | NullPointerException e) {
				return new ExceptionFactory().getResponseException("Unknown error", HttpStatusCode.valueOf(500));
			}
		}

		return new ExceptionFactory().getResponseException(ex.getMessage(), HttpStatusCode.valueOf(400));
	}
}