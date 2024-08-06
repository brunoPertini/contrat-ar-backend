package com.contractar.microserviciousuario.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public abstract class PredicateBasedRepository<T, K, U> {
	@PersistenceContext
	protected EntityManager entityManager;

	protected CriteriaBuilder criteriaBuilder;

	protected CriteriaQuery<T> criteriaQuery;

	protected Root<T> root;

	private Class<T> baseClass;

	PredicateBasedRepository(Class<T> baseClass) {
		this.baseClass = baseClass;
	}

	@PostConstruct
	public void init() {
		this.criteriaBuilder = entityManager.getCriteriaBuilder();
		this.criteriaQuery = this.criteriaBuilder.createQuery(baseClass);
		this.root = criteriaQuery.from(baseClass);
	}

	public abstract List<T> get(K id, U filters);
}
