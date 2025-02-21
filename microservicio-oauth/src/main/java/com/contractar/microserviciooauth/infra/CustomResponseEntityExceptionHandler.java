package com.contractar.microserviciooauth.infra;

import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.constants.CustomHeaders;
import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.SessionExpiredException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciooauth.exceptions.CodeWasAlreadyApplied;
import com.contractar.microserviciooauth.exceptions.CodeWasntRequestedException;
import com.contractar.microserviciooauth.exceptions.TwoFaCodeAlreadySendException;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { UserNotFoundException.class })
	public ResponseEntity<Object> handleUserNotFoundException(Exception ex) {
		UserNotFoundException castedException = (UserNotFoundException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.getStatusCode()));
	}

	@ExceptionHandler(value = { HttpClientErrorException.class })
	public ResponseEntity<Object> handleHttpClientErrorException(Exception ex) {
		HttpClientErrorException castedException = (HttpClientErrorException) ex;
		boolean isForbiddenException = castedException.getStatusCode().equals(HttpStatusCode.valueOf(403));
		boolean hasAccountStatusHeader = Optional.ofNullable(castedException.getResponseHeaders())
				.map(headers -> Optional.ofNullable(headers.get(CustomHeaders.ACCOUNT_STATUS))).isPresent();
		
		if (isForbiddenException && hasAccountStatusHeader) {
			return ResponseEntity.status(403)
					.header(CustomHeaders.ACCOUNT_STATUS, castedException.getResponseHeaders().get(CustomHeaders.ACCOUNT_STATUS).get(0))
					.body(castedException.getResponseBodyAsString());

		}

		return new ExceptionFactory().getResponseException(castedException.getResponseBodyAsString(),
				castedException.getStatusCode());

	}

	@ExceptionHandler(value = { SessionExpiredException.class, CodeWasAlreadyApplied.class,
			CodeWasntRequestedException.class, TwoFaCodeAlreadySendException.class })
	public ResponseEntity<Object> handle2FaExceptions(Exception ex) {
		if (ex instanceof TwoFaCodeAlreadySendException) {
			int statusCodeValue = ((TwoFaCodeAlreadySendException) ex).getStatusCode();
			HttpStatusCode statusCode = HttpStatusCode.valueOf(statusCodeValue);
			return new ExceptionFactory().getResponseException(ex.getMessage(), statusCode);
		}

		CustomException castedEx = (CustomException) ex;

		return new ExceptionFactory().getResponseException(castedEx.getMessage(),
				HttpStatusCode.valueOf(castedEx.getStatusCode()));
	}
}
