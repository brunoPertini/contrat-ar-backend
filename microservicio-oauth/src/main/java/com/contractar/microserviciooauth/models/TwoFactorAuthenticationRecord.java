package com.contractar.microserviciooauth.models;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TwoFactorAuthenticationRecord implements Serializable{
	
	private static final long serialVersionUID = -6139084222411894419L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	private int code;
	private Instant creationDateTime;
	private boolean wasChecked;
	
	public TwoFactorAuthenticationRecord() {}
	
	public TwoFactorAuthenticationRecord(Long userId, int code, Instant creationDateTime, boolean wasChecked) {
		this.userId = userId;
		this.code = code;
		this.creationDateTime = creationDateTime;
		this.wasChecked = wasChecked;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Instant getCreationDateTime() {
		return creationDateTime;
	}
	public void setCreationDateTime(Instant creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	public boolean isWasChecked() {
		return wasChecked;
	}
	public void setWasChecked(boolean wasChecked) {
		this.wasChecked = wasChecked;
	}
}
