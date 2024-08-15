package com.contractar.microserviciocommons.dto.vendibles;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
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
	private Page<AbstractProveedorVendibleDTOAccesor> vendibles;
	private Set<ProveedorDTO> proveedores;

	public VendibleProveedoresDTO(Page<AbstractProveedorVendibleDTOAccesor> vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}

	public VendibleProveedoresDTO() {
		this.vendibles = Page.empty();
		this.proveedores = new HashSet<>();
	}

	public Page<AbstractProveedorVendibleDTOAccesor> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Page<AbstractProveedorVendibleDTOAccesor> vendibles) {
		this.vendibles = vendibles;
	}

	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}


}
