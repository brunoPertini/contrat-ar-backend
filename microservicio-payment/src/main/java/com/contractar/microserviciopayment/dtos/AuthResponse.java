package com.contractar.microserviciopayment.dtos;

public class AuthResponse {

	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public AuthResponse() {}

	public AuthResponse(String accessToken) {
		this.accessToken = accessToken;
	}

}
