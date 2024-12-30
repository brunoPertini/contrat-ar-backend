package com.contractar.microserviciovendible.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.TransactionSystemException;

import com.contractar.microserviciousuario.models.Servicio;
import com.contractar.microserviciousuario.models.Vendible;


public interface ServicioRepository extends PagingAndSortingRepository<Servicio, Long>{
	public Servicio findById(Long id);

	public Servicio save(Vendible servicio) throws TransactionSystemException;
	
	public List<Servicio> findAll();
	
	@Query(value = "SELECT s FROM Servicio s JOIN FETCH ProveedorVendible pv WHERE :userRole='ADMIN' OR pv.state LIKE 'ACTIVE'")
	public List<Servicio> findAllOnlyWithActivePosts(@Param("userRole") String userRole);
	
	@Query(value = "SELECT DISTINCT s FROM Servicio s "
			+ "JOIN FETCH ProveedorVendible pv ON pv.vendible = s "
			+ "WHERE ((s.nombre LIKE %:nombre%) AND (:userRole='ADMIN' OR pv.state = 'ACTIVE'))")
	public List<Servicio> findByNombreContainingIgnoreCaseOrderByNombreAsc(@Param("nombre") String nombre, @Param("userRole") String userRole);

	
	@Query(value = "SELECT v.* FROM vendible v "
			+ "INNER JOIN proveedor_vendible pv ON (v.vendible_id=pv.vendible_id)"
			+ "INNER JOIN vendible_category vc ON (pv.category_id=vc.id)"
			+ "WHERE ((v.vendible_type = 'servicio') "
			+ "AND (:userRole = 'ADMIN' OR pv.state='ACTIVE') "
			+ "AND (:searchAttribute IS NULL OR (v.nombre LIKE CONCAT('%', :searchAttribute, '%')))  "
			+ "AND (vc.id=:categoryId))"
			+ "ORDER BY v.nombre ASC", nativeQuery = true)
	public List<Servicio> findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(@Param("searchAttribute") String nombre,
			@Param("categoryId") Long categoryId, @Param("userRole") String userRole);
}
