package com.contractar.microserviciovendible.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("servicio")
@Entity
public class Servicio extends Vendible {
	private static final long serialVersionUID = 9145596374367948234L;

	public Servicio () {

	}

	public Servicio(int precio, String descripcion, String nombre) {
		super(precio, descripcion, nombre);
	}
}
