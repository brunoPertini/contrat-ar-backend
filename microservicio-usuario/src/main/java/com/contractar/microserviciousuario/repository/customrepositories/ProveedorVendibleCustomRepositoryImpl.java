package com.contractar.microserviciousuario.repository.customrepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciovendible.models.VendibleCategory;
import com.contractar.microserviciovendible.repository.VendibleCategoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class ProveedorVendibleCustomRepositoryImpl implements ProveedorVendibleCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private VendibleCategoryRepository vendibleCategoryRepository;

	public ProveedorVendibleCustomRepositoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimplifiedVendibleDTO> getProveedorVendiblesInfo(Long proveedorId) {
		final String queryString = "SELECT pv.vendible_id as vendibleId, v.nombre as vendibleNombre,pv.descripcion as descripcion,"
				+ "pv.imagen_url as imagenUrl, vc.name as categoryName " + "FROM proveedor_vendible pv "
				+ "INNER JOIN vendible v ON (pv.vendible_id = v.vendible_id) "
				+ "INNER JOIN vendible_category vc ON (v.category_id = vc.id) " + "WHERE(pv.proveedor_id = ?1)";

		List<Object[]> results = entityManager.createNativeQuery(queryString)
				.setParameter(1, proveedorId)
				.getResultList();

		List<SimplifiedVendibleDTO> simplifiedVendibleDTOs = new ArrayList<SimplifiedVendibleDTO>();

		for (Object[] result : results) {
			SimplifiedVendibleDTO simplifiedVendibleDTO = new SimplifiedVendibleDTO();
			
			String categoryName = (String) result[4];
			
			VendibleCategory rootCategory = vendibleCategoryRepository.findByName(categoryName)
					.map(category -> category)
					.orElseGet(null);
			
			List<String> categoryNames = VendibleHelper.fetchHierachyForCategory(rootCategory)
					.stream()
					.map(category -> category.getName())
					.collect(Collectors.toList());

			simplifiedVendibleDTO.setVendibleId((Long) result[0]);
			simplifiedVendibleDTO.setVendibleNombre((String) result[1]);
			simplifiedVendibleDTO.setDescripcion((String) result[2]);
			simplifiedVendibleDTO.setImagenUrl((String) result[3]);
			simplifiedVendibleDTO.setCategoryNames(categoryNames);

			simplifiedVendibleDTOs.add(simplifiedVendibleDTO);
		}

		return simplifiedVendibleDTOs;

	}

}
