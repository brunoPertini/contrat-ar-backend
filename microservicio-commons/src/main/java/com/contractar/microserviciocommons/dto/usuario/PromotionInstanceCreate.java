package com.contractar.microserviciocommons.dto.usuario;

import jakarta.validation.constraints.NotNull;

public class PromotionInstanceCreate {

	@NotNull
	private Long suscriptionId;
	@NotNull
	private Long promotionId;
	
	private Long userId;
	
	public PromotionInstanceCreate() {}

	public PromotionInstanceCreate(Long suscriptionId, Long promotionId) {
		this.suscriptionId = suscriptionId;
		this.promotionId = promotionId;
	}
	
	public PromotionInstanceCreate(Long suscriptionId, Long promotionId, Long userId) {
		this(suscriptionId, promotionId);
		this.userId = userId;
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
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


}
