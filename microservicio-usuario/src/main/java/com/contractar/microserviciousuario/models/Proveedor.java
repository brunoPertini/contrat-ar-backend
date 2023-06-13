package com.contractar.microserviciousuario.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.plans.PlanType;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciovendible.models.Vendible;

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
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProveedorType proveedorType;

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
		this.vendibles = new ArrayList<Vendible>();
	}

	public Proveedor(Long id, String name, String surname, String email, boolean isActive,Point location,
			String dni, String password, PlanType plan, List<?> vendibles, LocalDate birthDate) {
		super(id, name, surname, email, isActive, location, birthDate, password);
		this.dni = dni;
		this.plan = plan;
		if (vendibles != null) {
			this.vendibles = vendibles;
		} else {
			this.vendibles = new ArrayList<Vendible>();
		}
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
	
	public ProveedorType getProveedorType() {
		return proveedorType;
	}

	public void setProveedorType(ProveedorType proveedorType) {
		this.proveedorType = proveedorType;
	}

}
