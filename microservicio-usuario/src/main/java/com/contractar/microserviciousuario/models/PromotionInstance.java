package com.contractar.microserviciousuario.models;

import java.time.LocalDate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class PromotionInstance {
	@EmbeddedId
	private PromotionInstanceId id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("promotionId")
	@JoinColumn(name = "promotion_id")
	private Promotion promotion;

	private LocalDate expirationDate;

	public PromotionInstanceId getId() {
		return id;
	}

	public void setId(PromotionInstanceId id) {
		this.id = id;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public PromotionInstance(PromotionInstanceId id, LocalDate expirationDate) {
		this.id = id;
		this.expirationDate = expirationDate;
	}
	
	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

}
