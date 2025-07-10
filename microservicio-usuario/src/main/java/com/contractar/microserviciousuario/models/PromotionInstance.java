package com.contractar.microserviciousuario.models;

import java.time.LocalDate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class PromotionInstance {
	@EmbeddedId
	private PromotionInstanceId id;

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

}
