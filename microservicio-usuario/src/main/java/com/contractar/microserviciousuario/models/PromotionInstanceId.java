package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PromotionInstanceId implements Serializable {
	private static final long serialVersionUID = 1855666377684294087L;

	private Long suscriptionId;
	private Long promotionId;
	
	public PromotionInstanceId() {}

	public PromotionInstanceId(Long suscriptionId, Long promotionId) {
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
