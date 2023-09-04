package com.contractar.microserviciovendible.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("producto")
@Entity
public class Producto extends Vendible {

	private static final long serialVersionUID = 8633680788289782676L;
	private int stock;

	public Producto() {

	}

	public Producto(int precio, String descripcion, String nombre, int stock) {
		super(nombre);
		this.stock = stock;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}


}
