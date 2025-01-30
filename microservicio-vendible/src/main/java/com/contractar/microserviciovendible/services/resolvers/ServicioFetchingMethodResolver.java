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
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, Long categoryId, String userRole) {
		return () -> {
			if(Optional.ofNullable(nombre).isEmpty() && Optional.ofNullable(categoryId).isEmpty()) {
				return this.servicioRepository.findAllOnlyWithActivePosts(userRole);
			}
			
		    if (Optional.ofNullable(categoryId).isPresent()) {
		        return Optional.ofNullable(vendibleService.findCategoryById(categoryId))
		                .map((category) -> this.servicioRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId(), userRole))
		                .orElseGet(() -> this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre, userRole));
		    } else {
		        return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre, userRole);
		    }
		};
	}

}
