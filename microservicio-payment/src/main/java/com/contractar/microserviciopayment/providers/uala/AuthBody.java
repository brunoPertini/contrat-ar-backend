package com.contractar.microserviciopayment.providers.uala;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthBody {

	private String username;
	
	@JsonProperty("client_id")
	private String clientId;
	
	@JsonProperty("client_secret_id")
	private String clientSecretId;

	@JsonProperty("grant_type")
	private final String grantType = "client_credentials";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecretId() {
		return clientSecretId;
	}

	public void setClientSecretId(String clientSecretId) {
		this.clientSecretId = clientSecretId;
	}

	public String getGrantType() {
		return grantType;
	}

	public AuthBody(String username, String clientId, String clientSecretId) {
		this.username = username;
		this.clientId = clientId;
		this.clientSecretId = clientSecretId;
	}

	public AuthBody() {}

}
