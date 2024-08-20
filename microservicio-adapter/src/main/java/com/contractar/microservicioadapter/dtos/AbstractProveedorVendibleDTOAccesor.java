package com.contractar.microservicioadapter.dtos;

import com.contractar.microservicioadapter.enums.PriceTypeInterface;

public interface AbstractProveedorVendibleDTOAccesor {
	public String getVendibleNombre();

	public String getDescripcion();

	public int getPrecio();

	public String getImagenUrl();

	public int getStock();
	
	public Long getVendibleId();
	
	public Long getVendibleCategoryId();
	
	public PriceTypeInterface getTipoPrecio();

	public boolean getOffersDelivery();
	
	public boolean isOffersInCustomAddress();
	
	public Long getPlanId();
}
