package com.contractar.microservicioadapter.entities;

import java.util.Set;

public interface VendibleAccesor {
	public String getNombre();
	
	public void setId(Long id);

	public Long getId();

	public Set<? extends ProveedorVendibleAccesor> getProveedoresVendibles();
}
