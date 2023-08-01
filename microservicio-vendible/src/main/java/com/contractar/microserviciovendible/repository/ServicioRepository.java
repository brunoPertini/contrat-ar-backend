package com.contractar.microserviciovendible.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciovendible.models.Servicio;

public interface ServicioRepository extends PagingAndSortingRepository<Servicio, Long>{
	public Servicio findById(Long id);

	public Servicio save(Servicio servicio);
	
	public List<Servicio> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
}
