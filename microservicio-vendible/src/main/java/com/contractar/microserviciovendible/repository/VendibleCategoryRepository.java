package com.contractar.microserviciovendible.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciovendible.models.VendibleCategory;

public interface VendibleCategoryRepository extends Repository<VendibleCategory, Long> {	
	VendibleCategory save(VendibleCategory category);

	
	Optional<VendibleCategory> findByNameIgnoreCase(String name);
	
	Optional<VendibleCategory> findByNameIgnoreCaseAndParentName(String name, String parent);
	
	List<VendibleCategory> findALlByNameIgnoreCase(String name);
	
	   @Query("SELECT vc FROM VendibleCategory vc "
	            + "LEFT JOIN vc.parent p1 "
	            + "LEFT JOIN p1.parent p2 "
	            + "WHERE vc.name = :categoryName "
	            + "AND (:parentName IS NULL OR p1.name = :parentName) "
	            + "AND (:grandparentName IS NULL OR p2.name = :grandparentName)")
	    VendibleCategory findByHierarchy(@Param("categoryName") String categoryName,
	                                     @Param("parentName") String parentName,
	                                     @Param("grandparentName") String grandparentName);
	   
	   Optional<VendibleCategory> findByParentName(String parentName);
	   
	   
	
    
}
