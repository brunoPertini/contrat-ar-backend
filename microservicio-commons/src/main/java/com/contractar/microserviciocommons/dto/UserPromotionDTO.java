package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

public class UserPromotionDTO {
	private String text;
	private LocalDate expirationDate;

	public UserPromotionDTO() {
	}

	public UserPromotionDTO(String text, LocalDate expirationDate) {
		this.text = text;
		this.expirationDate = expirationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

}
