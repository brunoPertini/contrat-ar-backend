package com.contractar.microserviciopayment.infra;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.payment.PaymentAlreadyDone;
import com.contractar.microserviciocommons.exceptions.payment.PaymentCantBeDone;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.infra.ExceptionFactory;

@ControllerAdvice
public class PaymentExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(value = {SuscriptionNotFound.class, PaymentAlreadyDone.class, PaymentCantBeDone.class})
	public ResponseEntity<?> handlePayExcerptions(Exception ex) {
		CustomException castedException = (CustomException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.getStatusCode()));
	}
}
