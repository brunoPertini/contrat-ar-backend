package com.contractar.microservicioadapter.entities;

import java.io.Serializable;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microservicioadapter.enums.PriceTypeInterface;


public interface ProveedorVendibleAccesor {
	public Serializable getId();

	public int getPrecio();

	public String getDescripcion();

	public String getImagenUrl();

	public int getStock();
	
	public void setVendible(VendibleAccesor vendible);

	public VendibleAccesor getVendible();

	public ProveedorAccessor getProveedor();

	public Point getLocation();

	public PriceTypeInterface getTipoPrecio();

	public boolean getOffersDelivery();

	public boolean getOffersInCustomAddress();

	public VendibleCategoryAccesor getCategory();

	public void setCategory(VendibleCategoryAccesor addedCategory);
	
	public void setState(PostState state);
	
	public PostState getState();
	
	public String getVendibleType();
	
}
