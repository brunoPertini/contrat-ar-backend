package com.contractar.microserviciovendible.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciovendible.models.VendibleCategory;

public interface VendibleCategoryRepository extends Repository<VendibleCategory, Long> {	
	VendibleCategory save(VendibleCategory category);

	Optional<VendibleCategory> findByName(String name);
	
	VendibleCategory findByNameAndParent(String name, VendibleCategory parent);
	
	@Query(value = "SELECT 1 FROM vendible_category firstTable WHERE (firstTable.id = :base_category_id)"
			+ " AND :first_parent_id = NULL OR EXISTS( SELECT 1 FROM contract_ar.vendible_category secondTable WHERE secondTable.id = firstTable.parent_id"
			+ " AND (secondTable.id = :first_parent_id) AND EXISTS(SELECT 1 "
			+ "FROM contract_ar.vendible_category thirdTable WHERE secondTable.parent_id = thirdTable.id "
			+ "AND (thirdTable.id = :second_parent_id)))",
			nativeQuery = true)
	boolean hierachyExists(@Param("base_category_id") Long firstCategoryId,
			@Param("first_parent_id") Long firstParentId,
			@Param("second_parent_id") Long secondParentId);
	
    
}
