package com.contractar.microserviciovendible.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;

	public List<ServicioDTO> findByNombreAsc(String nombre) {
		try {
			return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
					.map(servicio -> new ServicioDTO(servicio.getNombre(), servicio.getPrecio(),
							servicio.getDescripcion(), servicio.getImagesUrl(), servicio.getProveedores()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}
}
