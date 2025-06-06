package com.contractar.microserviciovendible.services.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.contractar.microserviciousuario.models.Vendible;
import com.contractar.microserviciovendible.repository.ProductoRepository;
import com.contractar.microserviciovendible.services.VendibleService;

@Component
public class ProductoFetchingMethodResolver implements VendibleFetchingMethodResolver {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, Long categoryId, String userRole) {
		return () -> {
			if(Optional.ofNullable(nombre).isEmpty() && Optional.ofNullable(categoryId).isEmpty()) {
				return this.productoRepository.findAllOnlyWithActivePosts(userRole);
			}
			
		    if (Optional.ofNullable(categoryId).isPresent()) {
		        return Optional.ofNullable(vendibleService.findCategoryById(categoryId))
		                .map((category) -> this.productoRepository.findByNombreAndCategoryContainingIgnoreCaseOrderByNombreAsc(nombre,
		                		category.getId(), userRole))
		                .orElseGet(() -> this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre, userRole));
		    } else {
		        return this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre, userRole);
		    }
		};
	}

}
