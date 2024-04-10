package com.contractar.microserviciovendible.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.TransactionSystemException;

import com.contractar.microserviciousuario.models.Producto;
import com.contractar.microserviciousuario.models.Vendible;

public interface ProductoRepository extends CrudRepository<Producto, Long>{
	public Producto save(Vendible producto) throws TransactionSystemException;
	
	public List<Producto> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
	
	@Query(value = "SELECT v.* FROM vendible v "
			+ "INNER JOIN proveedor_vendible pv ON (v.vendible_id=pv.vendible_id)"
			+ "INNER JOIN vendible_category vc ON (pv.category_id=vc.id)"
			+ "WHERE v.vendible_type = 'producto' "
			+ "AND v.nombre LIKE %:nombre%  "
			+ "AND vc.id=:categoryId "
			+ "ORDER BY v.nombre ASC", nativeQuery = true)
	public List<Producto> findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(@Param("nombre") String nombre,
			@Param("categoryId") Long categoryId);
}
