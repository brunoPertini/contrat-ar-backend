package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotNull;

public class ForgotPasswordMailInfo extends LinkMailInfo {
	@NotNull
	private String fullUserName;

	@NotNull
	private int expiresInMinutes;

	public ForgotPasswordMailInfo(String email, String token, String fullUserName, int expiresInMinutes) {
		super(email, token);
		this.fullUserName = fullUserName;
		this.expiresInMinutes = expiresInMinutes;
	}

	public String getFullUserName() {
		return fullUserName;
	}

	public void setFullUserName(String fullUserName) {
		this.fullUserName = fullUserName;
	}

	public int getExpiresInMinutes() {
		return expiresInMinutes;
	}

	public void setExpiresInMinutes(int expiresInMinutes) {
		this.expiresInMinutes = expiresInMinutes;
	}
}
