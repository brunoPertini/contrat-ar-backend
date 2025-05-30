package com.contractar.microserviciousuario.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("servicio")
@Entity
public class Servicio extends Vendible {
	private static final long serialVersionUID = 9145596374367948234L;

	public Servicio () {

	}

	public Servicio(String nombre) {
		super(nombre);
	}

	@Override
	public String getVendibleType() {
		return "servicios";
	}
}
