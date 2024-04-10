package com.contractar.microserviciovendible.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciousuario.models.VendibleCategory;

public interface VendibleCategoryRepository extends Repository<VendibleCategory, Long> {
	VendibleCategory save(VendibleCategory category);
	
	Optional<VendibleCategory> findById(Long id);

	Optional<VendibleCategory> findByNameIgnoreCase(String name);

	Optional<VendibleCategory> findByNameIgnoreCaseAndParentName(String name, String parent);

	List<VendibleCategory> findALlByNameIgnoreCase(String name);

	@Query("SELECT vc FROM VendibleCategory vc " + "LEFT JOIN vc.parent p1 " + "LEFT JOIN p1.parent p2 "
			+ "WHERE vc.name = :categoryName " + "AND (:parentName IS NULL OR p1.name = :parentName) "
			+ "AND (:grandparentName IS NULL OR p2.name = :grandparentName)")
	VendibleCategory findByHierarchy(@Param("categoryName") String categoryName, @Param("parentName") String parentName,
			@Param("grandparentName") String grandparentName);
	
	@Query(value = "SELECT * FROM contract_ar.vendible_category vc "
			+ "WHERE (vc.name LIKE :nombre) "
			+ "AND vc.parent_id IS NULL "
			+ "AND NOT EXISTS ("
			+ "	SELECT ven_cat.id "
			+ "   FROM contract_ar.vendible_category ven_cat "
			+ "   WHERE ven_cat.parent_id=vc.id"
			+ ")", nativeQuery = true)
	VendibleCategory findAloneCategory(@Param("nombre") String categoryName);

}
