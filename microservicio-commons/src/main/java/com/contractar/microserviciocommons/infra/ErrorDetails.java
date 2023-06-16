package com.contractar.microserviciocommons.infra;

import java.util.Date;

public final class ErrorDetails {
	@SuppressWarnings("unused")
	private Date timestamp;
	@SuppressWarnings("unused")
	private int status;
	@SuppressWarnings("unused")
	private String error;

	public ErrorDetails(Date timestamp, int status, String error) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
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
}
