package com.contractar.microserviciocommons.dto.proveedorvendible;

import java.util.Map;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProveedorVendibleUpdateDTO {

	private String descripcion;
	private String imagenUrl;
	private Integer precio;
	private PriceTypeValue tipoPrecio;

	private Boolean offersDelivery;
	private Boolean offersInCustomAddress;

	private Integer stock;

	private PostState state;

	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	private Point location;

	public ProveedorVendibleUpdateDTO(String descripcion, String imagenUrl, int precio, int stock,
			PriceTypeValue tipoPrecio) {
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.precio = precio;
		this.stock = stock;
		this.tipoPrecio = tipoPrecio;
	}

	public ProveedorVendibleUpdateDTO() {
	}

	public ProveedorVendibleUpdateDTO(PostState state) {
		this.state = state;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public Integer getPrecio() {
		return precio;
	}

	public void setPrecio(Integer precio) {
		this.precio = precio;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public PriceTypeValue getTipoPrecio() {
		return tipoPrecio;
	}

	public void setTipoPrecio(PriceTypeValue tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}

	public Boolean isOffersDelivery() {
		return offersDelivery;
	}

	public void setOffersDelivery(Boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}

	public Boolean isOffersInCustomAddress() {
		return offersInCustomAddress;
	}

	public void setOffersInCustomAddress(Boolean offersInCustomAddress) {
		this.offersInCustomAddress = offersInCustomAddress;
	}

	public PostState getState() {
		return state;
	}

	public void setState(PostState state) {
		this.state = state;
	}

	/**
	 * 
	 * @return A map containing DTO fields as keys. If the value is true, means it
	 *         need ADMIN approval
	 */
	public static Map<String, Boolean> proveedorVendibleUpdateStrategy() {
		return Map.of("descripcion", true, "imagenUrl", true, "location", true, "precio", false, "tipoPrecio", false,
				"offersDelivery", false, "offersInCustomAddress", false, "stock", false);
	}
}
