package com.contractar.microserviciocommons.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VendibleDTO {
	@JsonIgnore
	private Long id;
	private String nombre;
	protected Set<ProveedorVendibleDTO> proveedores;
	
	public VendibleDTO(String nombre) {
		this.nombre = nombre;
	}
	
	public VendibleDTO(Long id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	public VendibleDTO(String nombre, Set<ProveedorVendibleDTO> proveedores) {
		this.nombre = nombre;
		this.proveedores = proveedores;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<ProveedorVendibleDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<ProveedorVendibleDTO> proveedores) {
		this.proveedores = proveedores;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
