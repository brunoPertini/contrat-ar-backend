package com.contractar.microserviciopayment.providers.uala;

import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.serialization.UalaAuthResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = UalaAuthResponseDeserializer.class)
public class UalaAuthResponse extends AuthResponse{

	private int expiresIn;

	private String tokenType;

	public UalaAuthResponse(String accessToken, int expiresIn, String tokenType) {
		super(accessToken);
		this.expiresIn = expiresIn;
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public UalaAuthResponse() {
		super();
	}

}
