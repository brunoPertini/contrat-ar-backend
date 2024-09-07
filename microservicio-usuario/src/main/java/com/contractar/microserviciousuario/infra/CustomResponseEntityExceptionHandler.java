package com.contractar.microserviciousuario.infra;

import java.lang.reflect.InvocationTargetException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.ImageNotUploadedException;
import com.contractar.microserviciocommons.exceptions.UserCreationException;
import com.contractar.microserviciocommons.exceptions.UserInactiveException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleBindingException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciousuario.admin.services.ChangeAlreadyRequestedException;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { UserNotFoundException.class, EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleUserNotFoundException(Exception ex) {
		HttpStatus httpStatus = HttpStatus.valueOf(404);
		return ResponseEntity.status(httpStatus).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
	}
	
	@ExceptionHandler(value = { ImageNotUploadedException.class, UserCreationException.class,
			ClassNotFoundException.class, IllegalArgumentException.class,
			IllegalAccessException.class, InvocationTargetException.class,
			UserInactiveException.class})
	public ResponseEntity<Object> handleUsersUpdateExceptions(Exception ex) {
		HttpStatus httpStatus = HttpStatus.CONFLICT;
		return ResponseEntity.status(httpStatus).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(value = { VendibleBindingException.class,
			VendibleAlreadyBindedException.class,
			VendibleNotFoundException.class,
			VendibleAlreadyExistsException.class,
			VendibleUpdateException.class,
			ChangeAlreadyRequestedException.class,
			ChangeConfirmException.class})
	public ResponseEntity<Object> handleCustomExceptions(Exception ex) {
		CustomException castedException = (CustomException) ex;
		return new ExceptionFactory().getResponseException(castedException.getMessage(),
				HttpStatusCode.valueOf(castedException.getStatusCode()));
	}
}
