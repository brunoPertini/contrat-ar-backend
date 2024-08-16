package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;

public abstract class AbstractProveedorVendibleDTO implements AbstractProveedorVendibleDTOAccesor{
	
	private Long vendibleId;

	private String vendibleNombre;

	private String descripcion;

	private int precio;
	
	private PriceTypeValue tipoPrecio;
	
	private boolean offersDelivery;
	
	private boolean offersInCustomAddress;

	private String imagenUrl;

	private int stock;
	
	private Long vendibleCategoryId;
	

	public AbstractProveedorVendibleDTO(Long vendibleId, String vendibleNombre, String descripcion, int precio,
			PriceTypeValue tipoPrecio, boolean offersDelivery, boolean offersInCustomAddress, String imagenUrl,
			int stock, Long vendibleCategoryId) {
		this.vendibleId = vendibleId;
		this.vendibleNombre = vendibleNombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.tipoPrecio = tipoPrecio;
		this.offersDelivery = offersDelivery;
		this.offersInCustomAddress = offersInCustomAddress;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
		this.vendibleCategoryId = vendibleCategoryId;
	}

	protected AbstractProveedorVendibleDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl,
			int stock) {
		this.vendibleNombre = vendibleNombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
	}

	protected AbstractProveedorVendibleDTO() {
	}

	public String getVendibleNombre() {
		return vendibleNombre;
	}

	public void setVendibleNombre(String vendibleNombre) {
		this.vendibleNombre = vendibleNombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public int getStock() {
		return stock;
	}
	
	public Long getVendibleId() {
		return vendibleId;
	}

	public void setVendibleId(Long vendibleId) {
		this.vendibleId = vendibleId;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	public Long getVendibleCategoryId() {
		return vendibleCategoryId;
	}

	public void setVendibleCategoryId(Long vendibleCategoryId) {
		this.vendibleCategoryId = vendibleCategoryId;
	}
	
	public PriceTypeValue getTipoPrecio() {
		return tipoPrecio;
	}

	public void setTipoPrecio(PriceTypeValue tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}

	public boolean getOffersDelivery() {
		return offersDelivery;
	}

	public void setOffersDelivery(boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}
	
	public boolean isOffersInCustomAddress() {
		return offersInCustomAddress;
	}

	public void setOffersInCustomAddress(boolean offersInCustomAddress) {
		this.offersInCustomAddress = offersInCustomAddress;
	}

}
