package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.plans.PlanType;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProveedorDTO extends UsuarioDTO {
	@NotBlank
	private String dni;

	@JsonIgnore
	@NotNull
	private PlanType plan;

	@JsonIgnore
	@NotNull
	private ProveedorType proveedorType;

	public ProveedorDTO() {
	}

	public ProveedorDTO(String name, String surname, String email, boolean isActive, LocalDate birthDate, Role role,
			Point location, String dni, PlanType plan, ProveedorType proveedorType) {
		super(name, surname, email, isActive, birthDate, role, location);
		this.dni = dni;
		this.plan = plan;
		this.proveedorType = proveedorType;
	}

	public ProveedorDTO(Proveedor proveedor) {
		super(proveedor.getname(), proveedor.getsurname(), proveedor.getEmail(), proveedor.isActive(),
				proveedor.getBirthDate(), proveedor.getRole(), proveedor.getlocation());
		this.plan = proveedor.getPlan();
		this.proveedorType = proveedor.getProveedorType();
		Optional.ofNullable(proveedor.getId()).ifPresent((id) -> {
			this.setId(id);
		});
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		ProveedorDTO dto = (ProveedorDTO) obj;
		if (this.getId() != null) {
			return this.getId() == dto.getId();
		}
		return this.getEmail() != dto.getEmail();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getEmail());
	}
}
