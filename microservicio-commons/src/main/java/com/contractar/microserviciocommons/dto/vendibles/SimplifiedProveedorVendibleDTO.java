package com.contractar.microserviciocommons.dto.vendibles;

public class SimplifiedProveedorVendibleDTO extends AbstractProveedorVendibleDTO {
	private Long proveedorId;

	public SimplifiedProveedorVendibleDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl,
			int stock, Long proveedorId) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock);
		this.proveedorId = proveedorId;
	}

	public SimplifiedProveedorVendibleDTO() {
		super();
	}

	public Long getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Long proveedorId) {
		this.proveedorId = proveedorId;
	}

}
