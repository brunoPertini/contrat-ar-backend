package com.contractar.microserviciovendible.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.TransactionSystemException;

import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;

public interface ServicioRepository extends PagingAndSortingRepository<Servicio, Long>{
	public Servicio findById(Long id);

	public Servicio save(Vendible servicio) throws TransactionSystemException;
	
	public List<Servicio> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
}
