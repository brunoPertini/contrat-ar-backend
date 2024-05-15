package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

import com.contractar.microserviciocommons.plans.PlanType;

public class ProveedorSensibleInfoDTO extends UsuarioSensibleInfoDTO{
	private PlanType plan;

	public PlanType getPlan() {
		return plan;
	}

	public void setPlan(PlanType plan) {
		this.plan = plan;
	}

	public ProveedorSensibleInfoDTO(PlanType plan) {
		super();
		this.plan = plan;
	}
	
	
}
