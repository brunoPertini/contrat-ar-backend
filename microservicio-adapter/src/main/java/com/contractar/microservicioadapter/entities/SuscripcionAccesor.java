package com.contractar.microservicioadapter.entities;

import java.time.LocalDate;

import com.contractar.microservicioadapter.enums.PlanAccesor;

public interface SuscripcionAccesor {
	public Long getId();

	public boolean isActive();

	public void setActive(boolean value);

	public UsuarioAccesor getUsuario();

	public void setUsuario(UsuarioAccesor usuario);

	public PlanAccesor getPlan();

	public void setPlan(PlanAccesor plan);

	public LocalDate getCreatedDate();

	public void setCreatedDate(LocalDate date);
}
