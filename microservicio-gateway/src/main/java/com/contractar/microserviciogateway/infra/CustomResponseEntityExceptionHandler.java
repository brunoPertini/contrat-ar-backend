package com.contractar.microserviciogateway.infra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		String error = "";
		
        Class<? extends Exception> exceptionClass = ex.getClass();
        
        Method getReasonMethod;
		try {
			getReasonMethod = exceptionClass.getMethod("getReason");
		} catch (NoSuchMethodException e) {
			getReasonMethod = null;
		} catch (SecurityException e) {
			getReasonMethod = null;
		}

        if (getReasonMethod != null) {
        	try {
				error = (String) getReasonMethod.invoke(ex);
			} catch (IllegalAccessException e) {
				error = "";
			} catch (InvocationTargetException e) {
				error = "";
			}
        }
           
		ErrorDetails errorDetails = new ErrorDetails(new Date(), status.value(), error);
		return new ResponseEntity<>(errorDetails, headers, status);
	}

	private final class ErrorDetails {
		private Date timestamp;
		private int status;
		private String error;
		
		
		public Date getTimestamp() {
			return timestamp;
		}


		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}


		public int getStatus() {
			return status;
		}


		public void setStatus(int status) {
			this.status = status;
		}


		public String getError() {
			return error;
		}


		public void setError(String error) {
			this.error = error;
		}


		public ErrorDetails(Date timestamp, int status, String error) {
			super();
			this.timestamp = timestamp;
			this.status = status;
			this.error = error;
		}
	}
}
