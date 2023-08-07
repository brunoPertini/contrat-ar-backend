package com.contractar.microserviciocommons.infra;

import java.util.Date;

public final class ErrorDetails {
	@SuppressWarnings("unused")
	private Date timestamp;
	@SuppressWarnings("unused")
	private int status;
	@SuppressWarnings("unused")
	private String error;
	
	private Object relatedFields;
	
	public ErrorDetails(Date timestamp, int status, String error) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
	}

	public ErrorDetails(Date timestamp, int status, String error, Object relatedFields) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.relatedFields = relatedFields;
	}
	
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
	
	public Object getRelatedFields() {
		return relatedFields;
	}

	public void setRelatedFields(Object relatedFields) {
		this.relatedFields = relatedFields;
	}
}
