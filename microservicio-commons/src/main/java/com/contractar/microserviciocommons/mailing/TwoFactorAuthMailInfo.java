package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotNull;

public class TwoFactorAuthMailInfo extends MailInfo {
	@NotNull
	private int code;
	
	@NotNull
	private String fullUserName;
	
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
	
	public String getFullUserName() {
		return fullUserName;
	}

	public void setFullUserName(String fullUserName) {
		this.fullUserName = fullUserName;
	}

	
	public TwoFactorAuthMailInfo() {}

	public TwoFactorAuthMailInfo(String toAddress, int code, int expiresInMinutes, String fullUserName) {
		super(toAddress);
		this.code = code;
		this.expiresInMinutes = expiresInMinutes;
		this.fullUserName = fullUserName;
	}

}
