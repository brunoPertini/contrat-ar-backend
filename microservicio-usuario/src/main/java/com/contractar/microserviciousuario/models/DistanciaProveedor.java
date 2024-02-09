package com.contractar.microserviciousuario.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

/**
 * This entity class aims to provide the distance between the place where a proveedor
 * offers its product or service and the user's current location.
 */
@Entity
public class DistanciaProveedor {
	@EmbeddedId
	private ClienteProveedorVendibleId id;
	
	private double distance;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("clienteId")
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	public DistanciaProveedor() {
	}
	
	public DistanciaProveedor(ClienteProveedorVendibleId id, double distance, Proveedor proveedor, Cliente cliente) {
		this.id = id;
		this.distance = distance;
		this.cliente = cliente;
	} 
	
	public ClienteProveedorVendibleId getId() {
		return id;
	}

	public void setId(ClienteProveedorVendibleId id) {
		this.id = id;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}
