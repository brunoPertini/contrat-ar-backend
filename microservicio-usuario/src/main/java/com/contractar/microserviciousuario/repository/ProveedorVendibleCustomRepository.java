package com.contractar.microserviciousuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleFilter;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleFilteringStrategy;
import com.contractar.microserviciousuario.models.ProveedorVendible;

import jakarta.persistence.criteria.Predicate;

@Repository
public class ProveedorVendibleCustomRepository
		extends PredicateBasedRepository<ProveedorVendible, Long, ProveedorVendibleFilter> {

	ProveedorVendibleCustomRepository() {
		super(ProveedorVendible.class);
	}

	@Override
	public List<ProveedorVendible> get(Long vendibleId, ProveedorVendibleFilter filters, String rootEntityName) {
		ProveedorVendibleFilteringStrategy byPredicateStrategies = new ProveedorVendibleFilteringStrategy(
				criteriaBuilder, root, filters);

		List<Predicate> predicates = byPredicateStrategies.getAllStrategies();

		predicates.add(criteriaBuilder.equal(root.get(rootEntityName).get("id"), vendibleId));

		if (!predicates.isEmpty()) {
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		// If stock filter is applied, ordering by price and stock
		Optional.ofNullable(filters).map(ProveedorVendibleFilter::getMinStock).ifPresentOrElse(minStock -> {
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("precio")), criteriaBuilder.asc(root.get("stock")));
		}, () -> {
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("precio")));
		});

		return entityManager.createQuery(criteriaQuery).getResultList();
	}
}
