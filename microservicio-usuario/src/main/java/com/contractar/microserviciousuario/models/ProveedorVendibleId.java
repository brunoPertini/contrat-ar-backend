package com.contractar.microserviciousuario.models;

import java.io.Serializable;

public class ProveedorVendibleId implements Serializable{
	private static final long serialVersionUID = 5904666276558706286L;
	private Long proveedorId;
	private Long vendibleId;
	
	public ProveedorVendibleId() {
		
	}
	
	public ProveedorVendibleId(Long proveedorId, Long vendibleId) {
		this.proveedorId = proveedorId;
		this.vendibleId = vendibleId;
	}
	public Long getProveedorId() {
		return proveedorId;
	}
	public void setProveedorId(Long proveedorId) {
		this.proveedorId = proveedorId;
	}
	public Long getVendibleId() {
		return vendibleId;
	}
	public void setVendibleId(Long vendibleId) {
		this.vendibleId = vendibleId;
	}
}
