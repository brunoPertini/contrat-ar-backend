package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;

public class SimplifiedProveedorVendibleDTO extends AbstractProveedorVendibleDTO {
	private Long proveedorId;
	

	public SimplifiedProveedorVendibleDTO(Long vendibleId, Long proveedorId, String vendibleNombre, String descripcion, int precio,
			PriceTypeValue tipoPrecio, boolean offersDelivery, boolean offersInCustomAddress, String imagenUrl,
			int stock, Long vendibleCategoryId) {
		super(vendibleId, vendibleNombre, descripcion, precio, tipoPrecio, offersDelivery, offersInCustomAddress, imagenUrl,
				stock, vendibleCategoryId);
		this.proveedorId = proveedorId;
	}

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
