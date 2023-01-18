package com.contractar.microserviciovendible.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("producto")
@SuppressWarnings("serial")
@Entity
public class Producto extends Vendible {
	
	private int stock;
	
	public Producto() {
		
	}

	public Producto(int stock) {
		super();
		this.stock = stock;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	
}
