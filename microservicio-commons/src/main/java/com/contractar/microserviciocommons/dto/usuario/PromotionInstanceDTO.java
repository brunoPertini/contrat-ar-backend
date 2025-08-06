package com.contractar.microserviciocommons.dto.usuario;

import java.time.LocalDate;

public class PromotionInstanceDTO {
	private LocalDate expirationDate;
	private Long promotionId;
	private Long suscriptionId;

	public PromotionInstanceDTO() {
	}

	public PromotionInstanceDTO(Long promotionId, Long suscriptionId, LocalDate expirationDate) {
		this.expirationDate = expirationDate;
		this.promotionId = promotionId;
		this.suscriptionId = suscriptionId;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getSuscriptionId() {
		return suscriptionId;
	}

	public void setSuscriptionId(Long suscriptionId) {
		this.suscriptionId = suscriptionId;
	}
}
