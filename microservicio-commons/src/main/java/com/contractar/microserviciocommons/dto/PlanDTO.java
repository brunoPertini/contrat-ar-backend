package com.contractar.microserviciocommons.dto;

import com.contractar.microservicioadapter.enums.PlanType;

public class PlanDTO {
	private Long id;
	private String descripcion;
	private PlanType type;
	private int price;
	private double priceWithDiscount;

	public PlanDTO() {
	}

	public PlanDTO(Long id, String descripcion, PlanType type, int price, double priceWithDiscount) {
		this.id = id;
		this.descripcion = descripcion;
		this.type = type;
		this.price = price;
		this.priceWithDiscount = priceWithDiscount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public PlanType getType() {
		return type;
	}

	public void setType(PlanType type) {
		this.type = type;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public double getPriceWithDiscount() {
		return priceWithDiscount;
	}

	public void setPriceWithDiscount(double priceWithDiscount) {
		this.priceWithDiscount = priceWithDiscount;
	}

}
