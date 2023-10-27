package com.contractar.microserviciovendible.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private VendibleService vendibleService;

	public VendiblesResponseDTO findByNombreAsc(String nombre, String categoryName) { 
		VendiblesResponseDTO response = new VendiblesResponseDTO();
		
		Supplier<List<Producto>> repositoryFunction = () -> {
		    if (StringUtils.hasLength(categoryName)) {
		        return Optional.ofNullable(vendibleService.findCategoryByName(categoryName))
		                .map((category) -> this.productoRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId()))
		                .orElseGet(() -> this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre));
		    } else {
		        return this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);
		    }
		};
		
		
		repositoryFunction.get().stream().forEach(producto -> {
			Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper.getProveedoresVendibles(response,
					producto);
			if (proveedoresVendibles.size() > 0) {
				response.getVendibles().put(producto.getNombre(), proveedoresVendibles);
			}

			VendibleHelper.addCategoriasToResponse(producto, response);
		});

		return response;
	}

}
