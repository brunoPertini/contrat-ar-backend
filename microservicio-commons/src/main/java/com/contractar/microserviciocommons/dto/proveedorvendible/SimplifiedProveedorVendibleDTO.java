package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microservicioadapter.enums.PriceTypeInterface;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimplifiedProveedorVendibleDTO extends AbstractProveedorVendibleDTO {
	private Long proveedorId;
	
	@JsonIgnore
	private Long planId;

	public SimplifiedProveedorVendibleDTO(Long vendibleId, Long proveedorId, String vendibleNombre, String descripcion, int precio,
			PriceTypeInterface tipoPrecio, boolean offersDelivery, boolean offersInCustomAddress, String imagenUrl,
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
	
	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

}
