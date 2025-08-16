package com.contractar.microserviciopayment.dtos;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

import jakarta.validation.constraints.NotNull;

public class PaymentDTO {

	@NotNull
	private IntegrationType integrationType;
	
	// amout may be null for certain pay scenarios
	private int amount;
	
	// Used for the cases when a user changes its plan. As its not yet binded in proveedor side, have to persist it to not be lost.
	private Long toBeBindUserId;
	
	// Used to delete logically the promotion instance linked to the subscription.
	private Long promotionId;

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getToBeBindUserId() {
		return toBeBindUserId;
	}

	public void setToBeBindUserId(Long toBeBindUserId) {
		this.toBeBindUserId = toBeBindUserId;
	}

	public IntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(IntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public PaymentDTO(IntegrationType integrationType, int amount) {
		this.integrationType = integrationType;
		this.amount = amount;
	}

	public PaymentDTO() {}

}
