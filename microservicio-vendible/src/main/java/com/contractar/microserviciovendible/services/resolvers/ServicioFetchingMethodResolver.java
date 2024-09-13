package com.contractar.microserviciovendible.services.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.contractar.microserviciousuario.models.Vendible;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.services.VendibleService;

@Component
public class ServicioFetchingMethodResolver implements VendibleFetchingMethodResolver {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ServicioRepository servicioRepository;
	
	@Override
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, Long categoryId) {
		return () -> {
			if(Optional.ofNullable(nombre).isEmpty()) {
				return this.servicioRepository.findAllOnlyWithActivePosts();
			}
			
		    if (Optional.ofNullable(categoryId).isPresent()) {
		        return Optional.ofNullable(vendibleService.findCategoryById(categoryId))
		                .map((category) -> this.servicioRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId()))
		                .orElseGet(() -> this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre));
		    } else {
		        return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);
		    }
		};
	}

}
