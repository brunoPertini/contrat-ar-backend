package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class MailInfo {
	
	@NotBlank
	private String toAddress;

	public MailInfo() {

	}

	public MailInfo(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

}
