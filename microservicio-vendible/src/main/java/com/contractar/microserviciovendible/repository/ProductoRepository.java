package com.contractar.microserviciovendible.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.TransactionSystemException;

import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.models.Vendible;

public interface ProductoRepository extends CrudRepository<Producto, Long>{
	public Producto save(Vendible producto) throws TransactionSystemException;
}
