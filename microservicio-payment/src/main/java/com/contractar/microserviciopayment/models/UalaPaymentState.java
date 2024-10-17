package com.contractar.microserviciopayment.models;

import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(value = "1")
public class UalaPaymentState extends PaymentState {

	@Enumerated(EnumType.STRING)
	@NotNull
	private UalaPaymentStateValue state;

	public UalaPaymentStateValue getState() {
		return state;
	}

	public void setState(UalaPaymentStateValue state) {
		this.state = state;
	}

	public UalaPaymentState() {
	}

	public UalaPaymentState(UalaPaymentStateValue state) {
		super();
		this.state = state;
	}

}
