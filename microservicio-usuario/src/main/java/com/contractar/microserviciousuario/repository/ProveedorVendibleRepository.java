package com.contractar.microserviciousuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;

final class Queries {
	 static final String GET_POSTS_OF_PROVEEDOR = "SELECT pv FROM ProveedorVendible pv "
            + "JOIN pv.vendible "
            + "JOIN pv.category "
            + "WHERE pv.id.proveedorId=:proveedorId";
	 
	 static final String GET_POSTS_OF_VENDIBLE = "SELECT pv FROM ProveedorVendible pv "
	            + "JOIN pv.vendible "
	            + "JOIN pv.proveedor "
	            + "WHERE pv.id.vendibleId=:vendibleId";
	 
	 static final String GET_POSTS_OF_VENDIBLE_WITH_VALID_SUSCRIPTION = "SELECT pv from ProveedorVendible  pv "
	 		+ "JOIN pv.proveedor p "
	 		+ "JOIN p.suscripcion s "
	 		+ "WHERE pv.state LIKE 'ACTIVE' AND "
	 		+ "pv.vendible.id=:vendibleId "
	 		+ "AND p.active"
	 		+ " AND s.isActive "
	 		+ "ORDER BY s.plan DESC"; 
}

public interface ProveedorVendibleRepository extends PagingAndSortingRepository<ProveedorVendible, ProveedorVendibleId> {
	@SuppressWarnings("unchecked")
	public ProveedorVendible save(ProveedorVendible p);

	public void deleteById(ProveedorVendibleId id);

	public Optional<ProveedorVendible> findById(ProveedorVendibleId id);
	
	@Query(Queries.GET_POSTS_OF_PROVEEDOR)
	public List<ProveedorVendible> getProveedorVendibleInfo(@Param("proveedorId") Long proveedorId);
	
	@Query(Queries.GET_POSTS_OF_PROVEEDOR)
	public Page<ProveedorVendible> getProveedorVendibleInfo(@Param("proveedorId") Long proveedorId, Pageable pageable);
	
	@Query(Queries.GET_POSTS_OF_VENDIBLE)
	public List<ProveedorVendible> getProveedoreVendiblesInfoForVendible(@Param("vendibleId") Long vendibleId);
	
	@Query(Queries.GET_POSTS_OF_VENDIBLE)
	public Page<ProveedorVendible> getProveedoreVendiblesInfoForVendible(@Param("vendibleId") Long vendibleId, Pageable pageable);
	
	@Query(Queries.GET_POSTS_OF_VENDIBLE_WITH_VALID_SUSCRIPTION)
	public List<ProveedorVendible> getPostsOfProveedoresWithValidSubscription(@Param("vendibleId") Long vendibleId);
}
