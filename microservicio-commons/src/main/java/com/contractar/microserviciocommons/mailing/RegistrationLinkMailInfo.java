package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class RegistrationLinkMailInfo extends MailInfo {
	@NotBlank
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public RegistrationLinkMailInfo(String email, String token) {
		super(email);
		this.token = token;
	}
}
