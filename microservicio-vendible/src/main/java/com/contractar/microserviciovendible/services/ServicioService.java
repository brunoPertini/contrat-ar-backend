package com.contractar.microserviciovendible.services;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;

	public VendiblesResponseDTO findByNombreAsc(String nombre) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();
		this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream().forEach(servicio -> {
			Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper.getProveedoresVendibles(response,
					servicio);
			if (proveedoresVendibles.size() > 0) {
				response.getVendibles().put(servicio.getNombre(), proveedoresVendibles);
			}
		});

		return response;
	}
}
