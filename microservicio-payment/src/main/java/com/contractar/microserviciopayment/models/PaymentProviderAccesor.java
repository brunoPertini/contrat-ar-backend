package com.contractar.microserviciopayment.models;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

public interface PaymentProviderAccesor {
	public boolean isActive();

	public void setActive(boolean isActive);

	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);

	public IntegrationType getIntegrationType();

	public void setIntegrationType(IntegrationType integrationType);

}
