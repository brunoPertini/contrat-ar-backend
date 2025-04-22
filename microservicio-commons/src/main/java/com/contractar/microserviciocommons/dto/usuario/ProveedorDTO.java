package com.contractar.microserviciocommons.dto.usuario;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microservicioadapter.enums.Proveedor;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProveedorDTO extends UsuarioDTO {
	@NotBlank
	private String dni;

	@NotNull
	private SuscripcionDTO suscripcion;

	@JsonIgnore
	@NotNull
	private Proveedor proveedorType;
	
	private String fotoPerfilUrl;
	
	private boolean hasWhatsapp;
	
	public ProveedorDTO() {
	}

	public ProveedorDTO(Long id, String name, String surname, String email, boolean isActive, LocalDate birthDate,
			Point location, String dni, SuscripcionDTO suscripcion, ProveedorType proveedorType, String phone, String fotoPerfilUrl) {
		super(id, name, surname, email, isActive, birthDate, location, phone);
		this.dni = dni;
		this.suscripcion = suscripcion;
		this.proveedorType = proveedorType;
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public ProveedorDTO(ProveedorAccessor proveedor) {
		super(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(), proveedor.isActive(),
				proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getPhone());
		this.fotoPerfilUrl = proveedor.getFotoPerfilUrl();
		this.proveedorType = proveedor.getProveedorType();
		Optional.ofNullable(proveedor.getId()).ifPresent(this::setId);
		this.dni = proveedor.getDni();

		Optional.ofNullable(proveedor.getSuscripcion()).ifPresent(subscription -> {
			this.setSubscription(subscription, null);
		});
		
		this.hasWhatsapp = proveedor.hasWhatsapp();
	}
	
	public ProveedorDTO(ProveedorAccessor proveedor, @Nullable String subscriptionDatePattern) {
		super(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(), proveedor.isActive(),
				proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getPhone());
		this.fotoPerfilUrl = proveedor.getFotoPerfilUrl();
		this.proveedorType = proveedor.getProveedorType();
		Optional.ofNullable(proveedor.getId()).ifPresent(this::setId);
		this.dni = proveedor.getDni();
		Optional.ofNullable(proveedor.getSuscripcion()).ifPresent(subscription -> {
			this.setSubscription(subscription, subscriptionDatePattern);
		});
	}
	
	private void setSubscription(SuscripcionAccesor suscripcionAccesor, @Nullable String subscriptionDatePattern) {
		Optional.ofNullable(subscriptionDatePattern).ifPresentOrElse(pattern -> {
			this.suscripcion = new SuscripcionDTO(
					suscripcionAccesor.getId(),
					suscripcionAccesor.isActive(),
					suscripcionAccesor.getUsuario().getId(),
					suscripcionAccesor.getPlan().getId(),
					suscripcionAccesor.getCreatedDate(),
					pattern);
		}, () -> {
			this.suscripcion = new SuscripcionDTO(
					suscripcionAccesor.getId(),
					suscripcionAccesor.isActive(),
					suscripcionAccesor.getUsuario().getId(),
					suscripcionAccesor.getPlan().getId(),
					suscripcionAccesor.getCreatedDate());
		});
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public SuscripcionDTO getSuscripcion() {
		return suscripcion;
	}

	public void setSuscripcion(SuscripcionDTO suscripcion) {
		this.suscripcion = suscripcion;
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
	
	@JsonProperty("hasWhatsapp")
	public boolean hasWhatsapp() {
		return hasWhatsapp;
	}

	public void setHasWhatsapp(boolean hasWhatsapp) {
		this.hasWhatsapp = hasWhatsapp;
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
