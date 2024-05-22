package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import com.contractar.microservicioadapter.enums.PlanAccesor;
import com.contractar.microservicioadapter.enums.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Plan implements Serializable, PlanAccesor {

	private static final long serialVersionUID = -7609189133216465308L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 3000)
	@NotBlank
	private String descripcion;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private PlanType type;
	
	@Column
	private int price;
	
	public Plan() {}
		
	
	public Plan(Long id, String descripcion, PlanType type, int price) {
		this.id = id;
		this.descripcion = descripcion;
		this.type = type;
		this.price = price;
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


}
