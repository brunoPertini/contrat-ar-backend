package com.contractar.microserviciousuario.models;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.contractar.microservicioadapter.entities.VendibleAccesor;
import com.contractar.microserviciousuario.serialization.VendibleDeserializer;
import com.contractar.microserviciousuario.serialization.VendibleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vendible_type", discriminatorType = DiscriminatorType.STRING)
@JsonSerialize(using = VendibleSerializer.class)
@JsonDeserialize(using = VendibleDeserializer.class)
@Entity
public abstract class Vendible implements Serializable, VendibleAccesor {

	private static final long serialVersionUID = -6708815378872073493L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendible_id")
	private Long id;

	@Column(unique = true)
	@NotBlank
	private String nombre;

	@OneToMany(mappedBy = "vendible", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ProveedorVendible> proveedoresVendibles;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Set<ProveedorVendible> getProveedoresVendibles() {
		return proveedoresVendibles;
	}

	public void setProveedoresVendibles(Set<ProveedorVendible> proveedoresVendibles) {
		this.proveedoresVendibles = proveedoresVendibles;
	}

	public Vendible() {
		this.proveedoresVendibles = new LinkedHashSet<ProveedorVendible>();
	}

	public Vendible(String nombre) {
		this.nombre = nombre;
		this.proveedoresVendibles = new LinkedHashSet<ProveedorVendible>();
	}

	public Vendible(String nombre, Set<ProveedorVendible> proveedoresVendibles) {
		this.nombre = nombre;
		this.proveedoresVendibles = proveedoresVendibles;
	}
	
	public abstract String getVendibleType();

}
