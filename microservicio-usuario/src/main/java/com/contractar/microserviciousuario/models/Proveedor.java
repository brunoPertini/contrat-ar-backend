package com.contractar.microserviciousuario.models;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.serialization.ProveedorDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name = "proveedorId")
@JsonDeserialize(using = ProveedorDeserializer.class)
public class Proveedor extends Usuario implements ProveedorAccessor {

	private static final long serialVersionUID = -7439587233032181786L;

	@Column(length = 80, unique = true)
	@NotBlank
	private String dni;

	@OneToOne
	@JoinColumn(name = "suscripcion")
	private Suscripcion suscripcion;

	@Enumerated(EnumType.STRING)
	@NotNull
	private ProveedorType proveedorType;

	@OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ProveedorVendible> vendibles;

	@Column(columnDefinition = "BOOLEAN DEFAULT 0")
	private boolean hasWhatsapp;

	@NotBlank
	private String fotoPerfilUrl;

	public Set<ProveedorVendible> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Set<ProveedorVendible> vendibles) {
		this.vendibles = vendibles;
	}

	public Proveedor() {
		super();
	}

	public Proveedor(String name, String surname, String email, boolean isActive, Point location, String dni,
			String password, Suscripcion suscripcion, Set<ProveedorVendible> vendibles, LocalDate birthDate,
			List<GrantedAuthority> authorities, Role role, ProveedorType proveedorType, String fotoPerfilUrl,
			String phone) {
		super(name, surname, email, isActive, location, birthDate, password, authorities, role, phone);
		this.dni = dni;
		this.suscripcion = suscripcion;
		this.proveedorType = proveedorType;
		this.fotoPerfilUrl = fotoPerfilUrl;
		if (vendibles != null) {
			this.vendibles = vendibles;
		} else {
			this.vendibles = new LinkedHashSet<ProveedorVendible>();
		}
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public SuscripcionAccesor getSuscripcion() {
		return suscripcion;
	}

	public void setSuscripcion(Suscripcion suscripcion) {
		this.suscripcion = suscripcion;
	}

	public ProveedorType getProveedorType() {
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

	public boolean hasWhatsapp() {
		return hasWhatsapp;
	}

	public void setHasWhatsapp(boolean hasWhatsapp) {
		this.hasWhatsapp = hasWhatsapp;
	}

}
