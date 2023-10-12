package com.contractar.microserviciocommons.vendibles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.VendibleCategoryDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.models.VendibleCategory;

public final class VendibleHelper {
	private static VendibleCategoryDTO currentArrayCategory;
	private static VendibleCategoryDTO nextArrayCategory;

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
	 * If vendible has a category, its pushed to the node in response. Given that
	 * each Vendible is meant to have a meaningful category, the less abstract as
	 * it's possible, the method will also look for the vendible category parents
	 * and add them, so they're presented to the frontend to enhance the search.
	 */
	public static void addCategoriasToResponse(Vendible vendible, VendiblesResponseDTO response) {
		Optional.ofNullable(vendible.getCategory()).ifPresent(category -> {
			List<VendibleCategoryDTO> toAddCategories = new ArrayList<VendibleCategoryDTO>();

			// Filling array with each Vendible category
			Optional<VendibleCategory> parentOpt = Optional.ofNullable(category.getParent());
			Long parentId = parentOpt.isPresent() ? parentOpt.get().getId() : null;
			VendibleCategoryDTO dto = new VendibleCategoryDTO(category.getId(), category.getName(), parentId);
			toAddCategories.add(dto);

			while (parentOpt.isPresent()) {
				VendibleCategory parent = parentOpt.get();
				Long parentParentId = Optional.ofNullable(parent.getParent()).isPresent() ? parent.getParent().getId()
						: null;
				VendibleCategoryDTO parentDTO = new VendibleCategoryDTO(parent.getId(), parent.getName(),
						parentParentId);
				toAddCategories.add(parentDTO);
				parentOpt = Optional.ofNullable(parent.getParent());
			}

			VendibleCategoryDTO rootCategory = toAddCategories.get(toAddCategories.size() - 1);
			boolean isMainCategoryInTree = response.getCategorias().containsKey(rootCategory);

			if (!isMainCategoryInTree) {
				// Have to construct the hierachy
				Iterator<VendibleCategoryDTO> iterator = toAddCategories.iterator();
				VendibleCategoryDTO firstCategory = iterator.next();
				Optional<Long> expectedParentIdOpt = Optional.ofNullable(firstCategory.getParentId());

				CategoryHierarchy currentHierarchy = null;
				CategoryHierarchy nextHierarchy = null;
				VendibleCategoryDTO currentElement = firstCategory;

				while (iterator.hasNext()) {
					LinkedHashSet<CategoryHierarchy> children = new LinkedHashSet<CategoryHierarchy>();

					// Processing hierachy
					while (iterator.hasNext() && expectedParentIdOpt.isPresent()
							&& currentElement.getParentId().equals(expectedParentIdOpt.get())) {
						children.add(new CategoryHierarchy(currentElement));
						currentElement = iterator.next();
					}

					currentHierarchy = new CategoryHierarchy(currentElement, children);

					if (iterator.hasNext()) {
						nextHierarchy = new CategoryHierarchy(iterator.next());
						nextHierarchy.getChildren().add(currentHierarchy);
						response.getCategorias().put(nextHierarchy.getRoot(), nextHierarchy);
					} else {
						response.getCategorias().put(currentHierarchy.getRoot(), currentHierarchy);
					}

					expectedParentIdOpt = Optional.ofNullable(currentElement.getParentId());
				}
			} else {
				Collections.reverse(toAddCategories);
				int i = 0;
				CategoryHierarchy currentHierarchy = response.getCategorias().get(toAddCategories.get(0));
				boolean canContinueOverTree = true;

				while (i < toAddCategories.size()) {
					boolean arrayNextElementExists = false;

					try {
						nextArrayCategory = toAddCategories.get(i + 1);
						arrayNextElementExists = nextArrayCategory != null;
					} catch (IndexOutOfBoundsException e) {
						arrayNextElementExists = false;
						nextArrayCategory = null;
					}

					Optional<CategoryHierarchy> nextToProcess = !arrayNextElementExists ? Optional.empty()
							: currentHierarchy
							.getChildren()
							.stream()
							.filter(h -> h.getRoot().equals(nextArrayCategory))
							.findFirst();

					canContinueOverTree = nextToProcess.isPresent();

					if (canContinueOverTree) {
						currentHierarchy = nextToProcess.get();
					} else {
						if (nextArrayCategory != null) {
							CategoryHierarchy nextHierachy = new CategoryHierarchy(nextArrayCategory);
							currentHierarchy.getChildren().add(nextHierachy);
							currentHierarchy = nextHierachy;
						}
					}

					i = i + 1;
				}

			}

		});
	}

}
