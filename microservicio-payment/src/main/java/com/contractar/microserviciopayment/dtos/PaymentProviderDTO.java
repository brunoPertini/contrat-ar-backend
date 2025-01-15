package com.contractar.microserviciopayment.dtos;

import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.enums.IntegrationType;

public class PaymentProviderDTO {
	private Long id;
	private String name;
	private boolean isActive;
	private IntegrationType integrationType;
	
	public PaymentProviderDTO() {}
	
	public PaymentProviderDTO(PaymentProvider p) {
		this.id = p.getId();
		this.name = p.getName();
		this.isActive = p.isActive();
		this.integrationType = p.getIntegrationType();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public IntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(IntegrationType integrationType) {
		this.integrationType = integrationType;
	}
	
	
	
	
	
}
