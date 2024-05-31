package com.contractar.microserviciocommons.dto.usuario;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microservicioadapter.enums.Proveedor;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProveedorDTO extends UsuarioDTO {
	@NotBlank
	private String dni;

	@NotNull
	private PlanType plan;

	@JsonIgnore
	@NotNull
	private Proveedor proveedorType;
	
	private String fotoPerfilUrl;
	
	public ProveedorDTO() {
	}

	public ProveedorDTO(Long id, String name, String surname, String email, boolean isActive, LocalDate birthDate,
			Point location, String dni, PlanType plan, ProveedorType proveedorType, String phone, String fotoPerfilUrl) {
		super(id, name, surname, email, isActive, birthDate, location, phone);
		this.dni = dni;
		this.plan = plan;
		this.proveedorType = proveedorType;
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public ProveedorDTO(ProveedorAccessor proveedor) {
		super(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(), proveedor.isActive(),
				proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getPhone());
		this.plan = proveedor.getPlan().getType();
		this.fotoPerfilUrl = proveedor.getFotoPerfilUrl();
		this.proveedorType = proveedor.getProveedorType();
		Optional.ofNullable(proveedor.getId()).ifPresent(this::setId);
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

	public Proveedor getProveedorType() {
		return proveedorType;
	}

	public void setProveedorType(ProveedorType proveedorType) {
		this.proveedorType = proveedorType;
	}
	
	public String getFotoPerfilUrl() {
		return fotoPerfilUrl;
	}

	public void setFotoPerfilUrl(String fotoPerfilUrl) {
		this.fotoPerfilUrl = fotoPerfilUrl;
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
