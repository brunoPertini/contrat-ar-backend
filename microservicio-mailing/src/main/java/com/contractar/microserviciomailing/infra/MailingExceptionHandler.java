package com.contractar.microserviciomailing.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MailingExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {MailSendException.class, MailParseException.class})
	public ResponseEntity<?> handleExceptions(Exception ex) {
		return ResponseEntity.status(HttpStatus.valueOf(409))
				.contentType(MediaType.TEXT_PLAIN)
				.body(ex.getMessage());
	}
}
