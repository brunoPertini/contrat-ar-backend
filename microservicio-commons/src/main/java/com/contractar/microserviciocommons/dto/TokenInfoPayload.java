package com.contractar.microserviciocommons.dto;

import jakarta.validation.constraints.NotNull;

public class TokenInfoPayload {
	@NotNull
	private String sub;
	private TokenType type;
	
	@NotNull
	private Long userId;
	
	@NotNull
	private String roleName;

	public TokenInfoPayload() {
	}

	public TokenInfoPayload(String sub, TokenType type, Long userId, String roleName) {
		this.sub = sub;
		this.type = type;
		this.userId = userId;
		this.roleName = roleName;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
