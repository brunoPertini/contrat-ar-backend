package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;

/**
 * 
 * Response given to a cliente when enters into a vendible detail. Contains
 * proveedores offers, each one with its distance from cliente current's
 * location. It also has some additional information useful for frontend, such as
 * min and max distances found.
 *
 */
public class VendibleProveedoresDTO extends SliderDTO{
	private Set<AbstractProveedorVendibleDTOAccesor> vendibles;
	private Set<ProveedorDTO> proveedores;

	public VendibleProveedoresDTO(Set<AbstractProveedorVendibleDTOAccesor> vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}

	public VendibleProveedoresDTO() {
		this.vendibles = new HashSet<>();
		this.proveedores = new HashSet<>();
	}

	public VendibleProveedoresDTO(Comparator<AbstractProveedorVendibleDTOAccesor> comparator) {
		this.vendibles = new TreeSet<>(comparator);
		this.proveedores = new HashSet<>();
	}

	public Set<AbstractProveedorVendibleDTOAccesor> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Set<AbstractProveedorVendibleDTOAccesor> vendibles) {
		this.vendibles = vendibles;
	}

	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}


}
