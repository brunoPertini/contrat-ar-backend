package com.contractar.microserviciocommons.dto;

import jakarta.validation.constraints.NotNull;

public class TokenInfoPayload {
	@NotNull
	private String sub;
	private TokenType type;

	public TokenInfoPayload() {
	}

	public TokenInfoPayload(String sub, TokenType type) {
		this.sub = sub;
		this.type = type;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

}
