package com.contractar.microserviciopayment.models;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "outsite_payment_provider")
public class OutsitePaymentProviderImpl extends PaymentProvider implements OutsitePaymentProvider {

	private static final long serialVersionUID = -1427284459719948223L;

	@Column(columnDefinition = "VARCHAR(2000) DEFAULT ''")
	private String token;

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public void setToken(String token) {
		this.token = token;
	}

	public OutsitePaymentProviderImpl() {
	}

	public OutsitePaymentProviderImpl(String name, IntegrationType integrationType, String token) {
		super(name, integrationType);
		this.token = token;
	}

}
