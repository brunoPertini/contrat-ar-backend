package com.contractar.microserviciovendible.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciovendible.models.Vendible;

public interface VendibleRepository extends CrudRepository<Vendible, Long>{
	public Optional<Vendible> findById(Long id);
}
