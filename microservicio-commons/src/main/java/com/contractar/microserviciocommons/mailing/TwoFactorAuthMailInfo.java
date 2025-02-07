package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotNull;

public class TwoFactorAuthMailInfo extends MailInfo {
	@NotNull
	private int code;
	
	@NotNull
	private int expiresInMinutes;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getExpiresInMinutes() {
		return expiresInMinutes;
	}

	public void setExpiresInMinutes(int expiresInMinutes) {
		this.expiresInMinutes = expiresInMinutes;
	}
	
	public TwoFactorAuthMailInfo() {}

	public TwoFactorAuthMailInfo(String toAddress, int code, int expiresInMinutes) {
		super(toAddress);
		this.code = code;
		this.expiresInMinutes = expiresInMinutes;
	}

}
