package com.contractar.microserviciousuario.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microservicioadapter.enums.PlanAccesor;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

/**
 * A subscription is a record that proves a provider can use a certain plan. It is considered active
 * while there are successful payments from the createdDate until today. 
 */
@Entity
public class Suscripcion implements Serializable, SuscripcionAccesor {
	private static final long serialVersionUID = -4411764433424483924L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private boolean isActive;

	@OneToOne
	@JoinColumn(name = "usuario")
	private Usuario usuario;

	@OneToOne
	@JoinColumn(name = "plan")
	private Plan plan;

	private LocalDate createdDate;

	@OneToMany(mappedBy = "suscripcion", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Payment> payments;

	public Suscripcion() {
	}

	public Suscripcion(boolean isActive, Usuario usuario, Plan plan, LocalDate createdDate) {
		this.isActive = isActive;
		this.usuario = usuario;
		this.plan = plan;
		this.createdDate = createdDate;
		this.payments = new LinkedHashSet<>();
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
	public UsuarioAccesor getUsuario() {
		return usuario;
	}

	@Override
	public void setUsuario(UsuarioAccesor usuario) {
		this.usuario = (Usuario) usuario;
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
