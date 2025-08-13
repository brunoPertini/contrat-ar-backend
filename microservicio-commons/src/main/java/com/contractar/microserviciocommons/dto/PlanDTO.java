package com.contractar.microserviciocommons.dto;

import com.contractar.microservicioadapter.enums.PlanType;

public class PlanDTO {
	private Long id;
	private String descripcion;
	private PlanType type;
	private int price;

	private Long applicablePromotion;
	private Double priceWithDiscount;

	public PlanDTO() {
	}
	
	public PlanDTO(Long id, String descripcion, PlanType type, int price) {
		this.id = id;
		this.descripcion = descripcion;
		this.type = type;
		this.price = price;
	}


	public PlanDTO(Long id, String descripcion, PlanType type, int price, double priceWithDiscount) {
		this(id, descripcion, type, price);
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

	public Double getPriceWithDiscount() {
		return priceWithDiscount;
	}

	public void setPriceWithDiscount(Double priceWithDiscount) {
		this.priceWithDiscount = priceWithDiscount;
	}

	public Long getApplicablePromotion() {
		return applicablePromotion;
	}

	public void setApplicablePromotion(Long applicablePromotion) {
		this.applicablePromotion = applicablePromotion;
	}

}
