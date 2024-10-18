package com.contractar.microserviciopayment.dtos;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

public class PaymentDTO {

	private IntegrationType integrationType;
	private int amount;

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
