package com.contractar.microserviciousuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;

public interface ProveedorVendibleRepository extends CrudRepository<ProveedorVendible, ProveedorVendibleId> {
	@SuppressWarnings("unchecked")
	public ProveedorVendible save(ProveedorVendible p);

	public void deleteById(ProveedorVendibleId id);

	public Optional<ProveedorVendible> findById(ProveedorVendibleId id);
	
	@Query("SELECT pv FROM ProveedorVendible pv "
            + "JOIN pv.vendible "
            + "JOIN pv.category "
            + "WHERE pv.id.proveedorId=:proveedorId ")
	public List<ProveedorVendible> getProveedorVendibleInfo(@Param("proveedorId") Long proveedorId);
}
