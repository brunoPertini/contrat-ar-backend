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
	
	@Query(value = "SELECT EXISTS(SELECT 1"
			+ " FROM vendible_category firstTable"
			+ " WHERE (firstTable.name = @base_category_name)"
			+ " AND @first_parent_name IS NULL OR firstTable.parent_id IN ("
			+ " SELECT id FROM contract_ar.vendible_category secondTable"
			+ " WHERE secondTable.id = firstTable.parent_id"
			+ " AND (secondTable.name = @first_parent_name)"
			+ " AND @second_parent_name IS NULL OR secondTable.parent_id IN ("
			+ " SELECT id FROM contract_ar.vendible_category thirdTable"
			+ " WHERE secondTable.parent_id = thirdTable.id"
			+ " AND (thirdTable.name = @second_parent_name))"
			+ "))",
			nativeQuery = true)
	Long getHierachyCount(@Param("base_category_name") String firstCategoryName,
			@Param("first_parent_name") String firstParentName,
			@Param("second_parent_name") String secondParentName);
	
    
}
