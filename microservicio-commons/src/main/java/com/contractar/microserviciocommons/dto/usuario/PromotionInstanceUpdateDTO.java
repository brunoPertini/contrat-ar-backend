package com.contractar.microserviciocommons.dto.usuario;

import java.time.LocalDate;

public class PromotionInstanceUpdateDTO {
	public PromotionInstanceUpdateDTO() {
	}

	public PromotionInstanceUpdateDTO(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	private LocalDate expirationDate;

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}
}