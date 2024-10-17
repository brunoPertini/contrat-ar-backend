package com.contractar.microserviciousuario.models;

import java.io.Serializable;
import java.time.LocalDate;
import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microservicioadapter.enums.PlanAccesor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * A subscription is a record that proves a provider can use a certain plan. It
 * is considered active while there are successful payments from the createdDate
 * until today.
 */
@Entity
public class Suscripcion implements Serializable, SuscripcionAccesor {
	private static final long serialVersionUID = -4411764433424483924L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private boolean isActive;

	@OneToOne(mappedBy = "suscripcion")
	@JoinColumn(name = "usuario")
	private Proveedor usuario;

	@OneToOne
	@JoinColumn(name = "plan")
	private Plan plan;

	private LocalDate createdDate;

	public Suscripcion() {
	}

	public Suscripcion(boolean isActive, Proveedor usuario, Plan plan, LocalDate createdDate) {
		this.isActive = isActive;
		this.usuario = usuario;
		this.plan = plan;
		this.createdDate = createdDate;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public ProveedorAccessor getUsuario() {
		return usuario;
	}

	@Override
	public void setUsuario(ProveedorAccessor usuario) {
		this.usuario = (Proveedor) usuario;
	}

	@Override
	public PlanAccesor getPlan() {
		return plan;
	}

	@Override
	public void setPlan(PlanAccesor plan) {
		this.plan = (Plan) plan;
	}

	@Override
	public LocalDate getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Long getId() {
		return this.id;
	}
}
