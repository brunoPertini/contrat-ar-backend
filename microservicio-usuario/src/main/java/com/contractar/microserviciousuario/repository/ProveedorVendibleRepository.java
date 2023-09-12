package com.contractar.microserviciousuario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;

public interface ProveedorVendibleRepository extends CrudRepository<ProveedorVendible, ProveedorVendibleId>{
	@SuppressWarnings("unchecked")
	public ProveedorVendible save(ProveedorVendible p);
	
	public void deleteById(ProveedorVendibleId id);
	
	public Optional<ProveedorVendible> findById(ProveedorVendibleId id);
}
