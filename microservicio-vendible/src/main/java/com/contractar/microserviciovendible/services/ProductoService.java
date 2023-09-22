package com.contractar.microserviciovendible.services;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciovendible.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	public VendiblesResponseDTO findByNombreAsc(String nombre) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();
		this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream().forEach(producto -> {
			Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper.getProveedoresVendibles(response,
					producto);
			if (proveedoresVendibles.size() > 0) {
				response.getVendibles().put(producto.getNombre(), proveedoresVendibles);
			}
		});

		return response;
	}

}
