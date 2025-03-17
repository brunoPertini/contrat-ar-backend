package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SuscriptionValidityDTO {
	private boolean isValid;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate expirationDate;

	private boolean canBePayed;
	
	public SuscriptionValidityDTO() {}

	public SuscriptionValidityDTO(boolean isValid, LocalDate expirationDate) {
		this.isValid = isValid;
		this.expirationDate = expirationDate;
	}

	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isCanBePayed() {
		return canBePayed;
	}

	public void setCanBePayed(boolean canBePayed) {
		this.canBePayed = canBePayed;
	}
}
