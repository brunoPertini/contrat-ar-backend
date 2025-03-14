package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

public class SuscriptionValidityDTO {
	private boolean isValid;
	private LocalDate expirationDate;
	
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
}
