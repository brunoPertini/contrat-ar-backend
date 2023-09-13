package com.contractar.microserviciovendible.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.contractar.microserviciovendible.models.Vendible;

public interface VendibleRepository extends CrudRepository<Vendible, Long>{
	public Optional<Vendible> findById(Long id);
	
	@Query(value = "DELETE FROM proveedor_vendible WHERE vendible_id=:vendibleId", nativeQuery = true)
    @Modifying
    @Transactional
    public void deleteAllProvedoresAndVendiblesRelations(@Param("vendibleId") Long vendibleId);
	
	@Query(value = "SELECT vendible_type FROM vendible WHERE vendible_id=:vendibleId", nativeQuery = true)
	public String getVendibleTypeById(@Param("vendibleId") Long vendibleId);
}
