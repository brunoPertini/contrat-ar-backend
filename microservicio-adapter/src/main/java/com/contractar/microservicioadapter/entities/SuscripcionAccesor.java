package com.contractar.microservicioadapter.entities;

import java.time.LocalDate;

import com.contractar.microservicioadapter.enums.PlanAccesor;

public interface SuscripcionAccesor {
	public Long getId();

	public boolean isActive();

	public void setActive(boolean value);

	public ProveedorAccessor getUsuario();

	public void setUsuario(ProveedorAccessor usuario);

	public PlanAccesor getPlan();

	public void setPlan(PlanAccesor plan);

	public LocalDate getCreatedDate();

	public void setCreatedDate(LocalDate date);
}
