package com.contractar.usuarios.models;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "proveedor")
@PrimaryKeyJoinColumn(name = "proveedorId")
public class Proveedor extends Usuario {
	
	@Column(length = 80, unique = true)
	private String dni;
	
	@Column(length = 40)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private PlanType plan;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "proveedores_vendibles",
	joinColumns=@JoinColumn(name="proveedor_id"),
	inverseJoinColumns = @JoinColumn(name="vendible_id"),
	uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "role_id"})})
	private List<Vendible> vendibles;
	
	public Proveedor() {
		this.vendibles = new ArrayList<Vendible>();
	}

	public Proveedor(Long id, String nombre, String apellido, String email, boolean isActive,Point ubicacion,
			String dni, String password, PlanType plan, List<Vendible> vendibles) {
		super(id, nombre, apellido, email, isActive, ubicacion);
		this.dni = dni;
		this.password = password;
		this.plan = plan;
		this.vendibles = vendibles;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PlanType getPlan() {
		return plan;
	}

	public void setPlan(PlanType plan) {
		this.plan = plan;
	}

}
