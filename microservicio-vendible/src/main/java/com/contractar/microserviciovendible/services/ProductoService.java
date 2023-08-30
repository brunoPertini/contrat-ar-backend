package com.contractar.microserviciovendible.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.ProductoDTO;
import com.contractar.microserviciovendible.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	public List<ProductoDTO> findByNombreAsc(String nombre) {
		try {
			return this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
					.map(producto -> new ProductoDTO(producto.getNombre(), producto.getPrecio(),
							producto.getDescripcion(), producto.getImagesUrl(), producto.getProveedores(),
							producto.getStock()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}

}
