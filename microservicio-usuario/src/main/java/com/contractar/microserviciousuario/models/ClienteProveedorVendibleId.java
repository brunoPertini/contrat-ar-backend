package com.contractar.microserviciousuario.models;

import java.io.Serializable;

public class ClienteProveedorVendibleId implements Serializable{

	private static final long serialVersionUID = 7967504227750555671L;
	
	private ProveedorVendibleId proveedorVendibleId;
	private Long clienteId;
	
	public ClienteProveedorVendibleId() {
	}

	public ClienteProveedorVendibleId(ProveedorVendibleId proveedorVendibleId, Long clienteId) {
		this.proveedorVendibleId = proveedorVendibleId;
		this.clienteId = clienteId;
	}

	public ProveedorVendibleId getProveedorVendibleId() {
		return proveedorVendibleId;
	}

	public void setProveedorVendibleId(ProveedorVendibleId proveedorVendibleId) {
		this.proveedorVendibleId = proveedorVendibleId;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}
}
