package com.contractar.microserviciocommons.dto.usuario;

import jakarta.validation.constraints.NotNull;

public class PromotionInstanceCreate {

	@NotNull
	private Long suscriptionId;
	@NotNull
	private Long promotionId;

	public PromotionInstanceCreate(Long suscriptionId, Long promotionId) {
		this.suscriptionId = suscriptionId;
		this.promotionId = promotionId;
	}

	public Long getSuscriptionId() {
		return suscriptionId;
	}

	public void setSuscriptionId(Long suscriptionId) {
		this.suscriptionId = suscriptionId;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

}
