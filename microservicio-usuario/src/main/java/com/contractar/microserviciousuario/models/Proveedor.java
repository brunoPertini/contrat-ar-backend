package com.contractar.microserviciousuario.models;

import java.util.List;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name = "proveedorId")
public class Proveedor extends Usuario {

	private static final long serialVersionUID = -7439587233032181786L;

	@Column(length = 80, unique = true)
	@NotBlank
	private String dni;

	@Enumerated(EnumType.STRING)
	@NotNull
	private PlanType plan;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity = Vendible.class)
	@JoinTable(name = "proveedores_vendibles",
	joinColumns=@JoinColumn(name="proveedor_id"),
	inverseJoinColumns = @JoinColumn(name="vendible_id"))
	private List<?> vendibles;

	public List<?> getVendibles() {
		return vendibles;
	}

	public void setVendibles(List<?> vendibles) {
		this.vendibles = vendibles;
	}

	public Proveedor() {

	}

	public Proveedor(Long id, String nombre, String apellido, String email, boolean isActive,Point ubicacion,
			String dni, String password, PlanType plan, List<?> vendibles) {
		super(id, nombre, apellido, email, isActive, ubicacion);
		this.dni = dni;
		this.plan = plan;
		this.vendibles = vendibles;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public PlanType getPlan() {
		return plan;
	}

	public void setPlan(PlanType plan) {
		this.plan = plan;
	}

}
