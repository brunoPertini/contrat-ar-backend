package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MailNotificationResultBody extends MailInfo {
	
	@NotBlank
	private String userName;

	@NotNull
	private boolean result;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MailNotificationResultBody(String toAddress, boolean result, String userName) {
		super(toAddress);
		this.result = result;
		this.userName = userName;
	}
}
