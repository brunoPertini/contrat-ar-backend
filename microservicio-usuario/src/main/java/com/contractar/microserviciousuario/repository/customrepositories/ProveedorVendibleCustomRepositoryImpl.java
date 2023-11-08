package com.contractar.microserviciousuario.repository.customrepositories;

import java.util.List;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class ProveedorVendibleCustomRepositoryImpl implements ProveedorVendibleCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public ProveedorVendibleCustomRepositoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getProveedorVendiblesInfo(Long proveedorId) {
		final String queryString = "SELECT pv.vendible_id as vendibleId, v.nombre as vendibleNombre,pv.descripcion as descripcion,"
				+ "pv.imagen_url as imagenUrl, vc.name as categoryName " + "FROM proveedor_vendible pv "
				+ "INNER JOIN vendible v ON (pv.vendible_id = v.vendible_id) "
				+ "INNER JOIN vendible_category vc ON (v.category_id = vc.id) " + "WHERE(pv.proveedor_id = ?1)";

		return entityManager.createNativeQuery(queryString)
				.setParameter(1, proveedorId)
				.getResultList();
	}

}
