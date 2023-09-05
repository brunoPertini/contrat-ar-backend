package com.contractar.microserviciovendible.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("producto")
@Entity
public class Producto extends Vendible {

	private static final long serialVersionUID = 8633680788289782676L;

	public Producto() {

	}

	public Producto(String nombre) {
		super(nombre);
	}
}
