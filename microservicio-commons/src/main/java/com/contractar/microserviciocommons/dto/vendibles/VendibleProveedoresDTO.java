package com.contractar.microserviciocommons.dto.vendibles;

import java.util.ArrayList;
import java.util.List;
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
	private List<ProveedorDTO> proveedores;

	public VendibleProveedoresDTO(Page<AbstractProveedorVendibleDTOAccesor> vendibles, List<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}

	public VendibleProveedoresDTO() {
		this.vendibles = Page.empty();
		this.proveedores = new ArrayList<>();
	}

	public Page<AbstractProveedorVendibleDTOAccesor> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Page<AbstractProveedorVendibleDTOAccesor> vendibles) {
		this.vendibles = vendibles;
	}

	public List<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(List<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}


}
