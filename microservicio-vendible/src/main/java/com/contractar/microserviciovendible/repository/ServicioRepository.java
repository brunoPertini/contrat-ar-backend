package com.contractar.microserviciovendible.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.TransactionSystemException;

import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;

public interface ServicioRepository extends PagingAndSortingRepository<Servicio, Long>{
	public Servicio findById(Long id);

	public Servicio save(Vendible servicio) throws TransactionSystemException;
	
	public List<Servicio> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
	
	@Query(value = "SELECT * FROM vendible v WHERE v.vendible_type = 'servicio' AND"
			+ " lower(v.nombre) LIKE %:nombre%  AND v.category_id = :categoryId ORDER BY v.nombre ASC", nativeQuery = true)
	public List<Servicio> findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(@Param("nombre") String nombre,
			@Param("categoryId") Long categoryId);
}
