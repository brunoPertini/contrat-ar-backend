package com.contractar.microserviciovendible.models;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.contractar.microserviciocommons.dto.vendibles.CategorizableObject;
import com.contractar.microserviciousuario.models.ProveedorVendible;

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
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vendible_type", discriminatorType = DiscriminatorType.STRING)
@Entity
public class Vendible implements Serializable, CategorizableObject {

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
	
	@OneToOne
	@NotNull
	private VendibleCategory category;

	@Override
	public VendibleCategory getCategory() {
		return category;
	}

	@Override
	public void setCategory(VendibleCategory category) {
		this.category = category;
	}

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

	public Set<ProveedorVendible> getProveedoresVendibles() {
		return proveedoresVendibles;
	}

	public void setProveedoresVendibles(Set<ProveedorVendible> proveedoresVendibles) {
		this.proveedoresVendibles = proveedoresVendibles;
	}
}
