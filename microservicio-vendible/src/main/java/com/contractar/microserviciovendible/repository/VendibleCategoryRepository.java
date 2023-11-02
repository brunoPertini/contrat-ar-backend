package com.contractar.microserviciovendible.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.contractar.microserviciovendible.models.VendibleCategory;

public interface VendibleCategoryRepository extends Repository<VendibleCategory, Long> {
	Optional<VendibleCategory> findByName(String name);
}
