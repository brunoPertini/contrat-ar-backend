package com.contractar.microserviciousuario.admin.dtos;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;
import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;
import com.contractar.microservicioadapter.enums.PriceTypeInterface;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.contractar.microserviciousuario.models.VendibleCategory;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class ProveedorVendibleAdminDTO {
	private String vendibleNombre;

	private String descripcion;

	private int precio;
	
	private PriceTypeInterface tipoPrecio;
	
	private boolean offersDelivery;
	
	private boolean offersInCustomAddress;

	private String imagenUrl;

	private int stock;
	
	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	private Point location;
	
	private VendibleCategoryAccesor category;
	
	public ProveedorVendibleAdminDTO() {}
 
	public ProveedorVendibleAdminDTO(ProveedorVendibleAccesor proveedorVendible) {
		this.vendibleNombre = proveedorVendible.getVendible().getNombre();
		this.descripcion = proveedorVendible.getDescripcion();
		this.precio = proveedorVendible.getPrecio();
		this.tipoPrecio = proveedorVendible.getTipoPrecio();
		this.offersDelivery = proveedorVendible.getOffersDelivery();
		this.offersInCustomAddress = proveedorVendible.getOffersInCustomAddress();
		this.imagenUrl = proveedorVendible.getImagenUrl();
		this.stock = proveedorVendible.getStock();
		this.location = proveedorVendible.getLocation();
		this.category = proveedorVendible.getCategory();
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

	public PriceTypeInterface getTipoPrecio() {
		return tipoPrecio;
	}

	public void setTipoPrecio(PriceTypeInterface tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}

	public boolean isOffersDelivery() {
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

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public VendibleCategoryAccesor getCategory() {
		return category;
	}

	public void setCategory(VendibleCategory category) {
		this.category = category;
	}
	
	
	
}
