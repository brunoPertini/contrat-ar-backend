package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class LinkMailInfo extends MailInfo {
	@NotBlank
	private String token;
	
	public LinkMailInfo() {}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public LinkMailInfo(String email, String token) {
		super(email);
		this.token = token;
	}
}
