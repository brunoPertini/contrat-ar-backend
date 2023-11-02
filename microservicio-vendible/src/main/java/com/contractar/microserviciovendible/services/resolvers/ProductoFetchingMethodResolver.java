package com.contractar.microserviciovendible.services.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.repository.ProductoRepository;
import com.contractar.microserviciovendible.services.VendibleService;

@Component
public class ProductoFetchingMethodResolver implements VendibleFetchingMethodResolver {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, String categoryName) {
		return () -> {
		    if (StringUtils.hasLength(categoryName)) {
		        return Optional.ofNullable(vendibleService.findCategoryByName(categoryName))
		                .map((category) -> this.productoRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId()))
		                .orElseGet(() -> this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre));
		    } else {
		        return this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);
		    }
		};
	}

}
