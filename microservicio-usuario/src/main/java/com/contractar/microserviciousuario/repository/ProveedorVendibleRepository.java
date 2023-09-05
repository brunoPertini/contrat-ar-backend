package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.ProveedorVendible;

public interface ProveedorVendibleRepository extends CrudRepository<ProveedorVendible, Long>{
	@SuppressWarnings("unchecked")
	public ProveedorVendible save(ProveedorVendible p);
}
