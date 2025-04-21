package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class ProveedorMessageBody extends MailInfo {
	@NotBlank
	private String clienteMail;

	@NotBlank
	private String vendibleName;

	@NotBlank
	private String message;

	public ProveedorMessageBody(String toAddress, String clienteMail, String vendibleName, String message) {
		super(toAddress);
		this.clienteMail = clienteMail;
		this.vendibleName = vendibleName;
		this.message = message;
	}

	public String getClienteMail() {
		return clienteMail;
	}

	public void setClienteMail(String clienteMail) {
		this.clienteMail = clienteMail;
	}

	public String getVendibleName() {
		return vendibleName;
	}

	public void setVendibleName(String vendibleName) {
		this.vendibleName = vendibleName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
