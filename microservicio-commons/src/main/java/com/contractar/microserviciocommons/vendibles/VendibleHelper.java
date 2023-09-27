package com.contractar.microserviciocommons.vendibles;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleCategoryDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.models.VendibleCategory;

public final class VendibleHelper {

	public static Set<SimplifiedProveedorVendibleDTO> getProveedoresVendibles(VendiblesResponseDTO response,
			Vendible vendible) {
		return vendible.getProveedoresVendibles().stream().map(proveedorVendible -> {
			Proveedor proveedor = proveedorVendible.getProveedor();
			SimplifiedProveedorVendibleDTO proveedorVendibleDTO = new SimplifiedProveedorVendibleDTO(
					vendible.getNombre(), proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
					proveedorVendible.getImagenUrl(), proveedorVendible.getStock(), proveedor.getId());
			response.getProveedores().add(new ProveedorDTO(proveedor));
			
			Optional.ofNullable(vendible.getCategory()).ifPresent(category -> {
				proveedorVendibleDTO.setVendibleCategoryId(category.getId());
			});

			return proveedorVendibleDTO;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * If vendible has a category, its added to the node in response. Given that each Vendible is meant to have a meaningful category,
	 * the less abstract as it's possible, the method will also look for the vendible category parents and add them, so they're presented 
	 * to the frontend to enhance the search.
	 */
	public static void addCategoriasToResponse(Vendible vendible, VendiblesResponseDTO response) {
		// TODO: No agregar a response. Agregarlo a un SET aparte de VendibleCategory, y despues iterar sobre el set ordenado para parsear cada elemento a un DTO
		Optional.ofNullable(vendible.getCategory()).ifPresent(category -> {
			Optional<VendibleCategory> parentOpt = Optional.ofNullable(category.getParent());
			Long parentId = parentOpt.isPresent() ? parentOpt.get().getId() : null;
			VendibleCategoryDTO dto = new VendibleCategoryDTO(category.getId(),category.getName(), parentId);
			response.getCategorias().add(dto);
			while (parentOpt.isPresent()) {
				VendibleCategory parent = parentOpt.get();
				Long parentParentId = Optional.ofNullable(parent.getParent()).isPresent() ? parent.getParent().getId() : null;
				VendibleCategoryDTO parentDTO = new VendibleCategoryDTO(parent.getId(),
						parent.getName(),
						parentParentId);				
				response.getCategorias().add(dto);
				parentOpt = Optional.ofNullable(parent.getParent());
			}
		});
	}

}
