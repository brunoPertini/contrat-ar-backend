package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.contractar.microservicioadapter.enums.PromotionType;

public class UserPromotionDTO {
	private String text;
	private LocalDate expirationDate;
	private PromotionType promotionType;

	public UserPromotionDTO() {
	}

	public UserPromotionDTO(String text, LocalDate expirationDate, PromotionType promotionType) {
		this.text = text;
		this.expirationDate = expirationDate;
		this.promotionType = promotionType;
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

	public PromotionType getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(PromotionType promotionType) {
		this.promotionType = promotionType;
	}

}
