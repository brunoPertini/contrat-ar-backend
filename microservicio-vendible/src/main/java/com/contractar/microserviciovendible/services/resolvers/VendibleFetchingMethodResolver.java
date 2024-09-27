package com.contractar.microserviciovendible.services.resolvers;

import java.util.List;
import java.util.function.Supplier;

import com.contractar.microserviciousuario.models.Vendible;

/**
 * 
 * Producto and Servicio have some behaviors that can be modularized in a class to
 * avoid repeating code. This interface sets the methods that an implementing class
 * should have.
 */
public interface VendibleFetchingMethodResolver {
	/**
	 *  
	 * @param nombre
	 * @param categoryId
	 * @return A supplier that runs the concrete repository method to find a vendible type by name and/or category 
	 */
	public Supplier<List<? extends Vendible>> getFindByNombreRepositoryMethod(String nombre, Long categoryId, String userRole);
	
}
