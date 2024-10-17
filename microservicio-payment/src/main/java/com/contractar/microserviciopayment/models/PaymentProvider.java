package com.contractar.microserviciopayment.models;

import java.io.Serializable;

import com.contractar.microserviciopayment.models.enums.IntegrationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class PaymentProvider implements Serializable {
	private static final long serialVersionUID = 748035295073393971L;

	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String name;

	@Enumerated(EnumType.STRING)
	@NotNull
	private IntegrationType integrationType;
	
	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public IntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(IntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public PaymentProvider() {
	}

	public PaymentProvider(String name, IntegrationType integrationType) {
		this.name = name;
		this.integrationType = integrationType;
	}

}
