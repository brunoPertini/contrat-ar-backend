package com.contractar.microserviciovendible.services.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.services.VendibleService;

@Component
public class ServicioFetchingMethodResolver implements VendibleFetchingMethodResolver {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ServicioRepository servicioRepository;
	
	@Override
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, String categoryName) {
		return () -> {
		    if (StringUtils.hasLength(categoryName)) {
		        return Optional.ofNullable(vendibleService.findCategoryByName(categoryName))
		                .map((category) -> this.servicioRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId()))
		                .orElseGet(() -> this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre));
		    } else {
		        return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);
		    }
		};
	}

}
