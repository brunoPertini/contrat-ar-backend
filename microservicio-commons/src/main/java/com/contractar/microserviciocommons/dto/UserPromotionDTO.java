package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.contractar.microservicioadapter.enums.PromotionType;

public class UserPromotionDTO {
	private Long promotionId;
	private String text;
	private LocalDate expirationDate;
	private PromotionType promotionType;

	public UserPromotionDTO() {
	}

	public UserPromotionDTO(String text, LocalDate expirationDate, PromotionType promotionType, Long promotionId) {
		this.text = text;
		this.expirationDate = expirationDate;
		this.promotionType = promotionType;
		this.promotionId = promotionId;
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
	
	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

}
