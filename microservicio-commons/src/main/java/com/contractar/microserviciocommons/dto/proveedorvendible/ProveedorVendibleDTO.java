package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;

public class ProveedorVendibleDTO extends AbstractProveedorVendibleDTO {
	private ProveedorDTO proveedor;

	public ProveedorVendibleDTO() {
		super();
	}

	public ProveedorVendibleDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl, int stock,
			ProveedorDTO proveedor) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock);
		this.proveedor = proveedor;
	}

	public ProveedorDTO getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}
}
