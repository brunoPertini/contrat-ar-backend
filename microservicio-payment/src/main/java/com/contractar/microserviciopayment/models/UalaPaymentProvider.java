package com.contractar.microserviciopayment.models;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

import jakarta.persistence.Entity;

@Entity
public class UalaPaymentProvider extends PaymentProvider {
	
	private static final long serialVersionUID = -1427284459719948223L;
	
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UalaPaymentProvider() {}

	public UalaPaymentProvider(String name, IntegrationType integrationType, String token) {
		super(name, integrationType);
		this.token = token;
	}

}
