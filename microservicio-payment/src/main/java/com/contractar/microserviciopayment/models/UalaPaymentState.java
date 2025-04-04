package com.contractar.microserviciopayment.models;

import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "1")
public class UalaPaymentState extends PaymentState {

	private static final long serialVersionUID = -8122212754544771967L;

	public UalaPaymentState() {
	}

	public UalaPaymentState(UalaPaymentStateValue state) {
		this.state = state.name();
	}

	@Override
	public String toString() {
		return this.state;
	}

}
