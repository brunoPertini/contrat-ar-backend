package com.contractar.microserviciousuario.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Proveedor;

public interface ProveedorRepository  extends CrudRepository<Proveedor, Long>{
	@SuppressWarnings("unchecked")
	public Proveedor save(Proveedor proveedor);
    
    @Query(value = "INSERT INTO proveedores_vendibles (proveedor_id, vendible_id) VALUES (:proveedorId, :vendibleId)",
    		nativeQuery = true)
    @Modifying
    @Transactional
    public void addVendible(@Param("proveedorId") Long proveedorId,
    		@Param("vendibleId") Long vendibleId);
    
    public boolean existsByIdAndProveedorType(Long id, ProveedorType proveedorType);
    
    public void deleteById(Long id);
}
