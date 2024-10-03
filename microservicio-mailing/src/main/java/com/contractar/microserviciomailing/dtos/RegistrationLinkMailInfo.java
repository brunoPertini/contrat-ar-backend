package com.contractar.microserviciomailing.dtos;

public class RegistrationLinkMailInfo extends MailInfo {
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
