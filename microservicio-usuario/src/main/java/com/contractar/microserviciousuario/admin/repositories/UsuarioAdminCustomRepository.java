package com.contractar.microserviciousuario.admin.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciocommons.dto.UsuarioFiltersDTO;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class UsuarioAdminCustomRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public List<? extends Usuario> getFilteredUsuarios(@NonNull String usuarioType, UsuarioFiltersDTO filters)
			throws IllegalAccessException {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Class<? extends Usuario> resolvedClass = usuarioType.equals("proveedores") ? Proveedor.class : Cliente.class;
		CriteriaQuery<? extends Usuario> cq = cb.createQuery(resolvedClass);

		Root<? extends Usuario> usuario = cq.from(resolvedClass);
		List<Predicate> predicates = new ArrayList<>();

		Map<String, Object> filtersFields = ReflectionHelper.getObjectFields(filters);

		if (!filtersFields.isEmpty()) {
			filtersFields.entrySet().forEach(entry -> {
				predicates.add(cb.like(usuario.get(entry.getKey()), "%" + entry.getValue().toString() + "%"));
			});

			cq.where(predicates.toArray(new Predicate[0]));
		}

		return entityManager.createQuery(cq).getResultList();
	}
}
