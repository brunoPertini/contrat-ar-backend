package com.contractar.microserviciovendible.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("servicio")
@SuppressWarnings("serial")
@Entity
public class Servicio extends Vendible {
	public Servicio () {
		
	}
}
