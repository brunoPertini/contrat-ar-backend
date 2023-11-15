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
import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableObject;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableVendiblesResponse;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.VendibleCategoryDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.models.VendibleCategory;

public final class VendibleHelper {
	private static VendibleCategoryDTO nextArrayCategory;

	/**
	 * 
	 * @param category
	 * @return the category hierachy as a List, starting from itself to the highest one, i.e., that without parent
	 */
	public static List<VendibleCategoryDTO> fetchHierachyForCategory(VendibleCategory category) {
		List<VendibleCategoryDTO> toAddCategories = new ArrayList<VendibleCategoryDTO>();

		Optional<VendibleCategory> parentOpt = Optional.ofNullable(category.getParent());
		Long parentId = parentOpt.isPresent() ? parentOpt.get().getId() : null;
		VendibleCategoryDTO dto = new VendibleCategoryDTO(category.getId(), category.getName(), parentId);
		toAddCategories.add(dto);

		while (parentOpt.isPresent()) {
			VendibleCategory parent = parentOpt.get();
			Long parentParentId = Optional.ofNullable(parent.getParent()).isPresent() ? parent.getParent().getId()
					: null;
			VendibleCategoryDTO parentDTO = new VendibleCategoryDTO(parent.getId(), parent.getName(), parentParentId);
			toAddCategories.add(parentDTO);
			parentOpt = Optional.ofNullable(parent.getParent());
		}

		return toAddCategories;
	}

	/**
	 * Receives ordered categories in descending order for inserting them as a new 
	 * hierachy, where the root element is the first element of the array.
	 * @param response
	 * @param toAddCategories
	 */
	private static void createAndInsertCategoryHierachy(CategorizableVendiblesResponse response,
			List<VendibleCategoryDTO> toAddCategories) {
		Iterator<VendibleCategoryDTO> iterator = toAddCategories.iterator();

		VendibleCategoryDTO currentElement = iterator.next();
		CategoryHierarchy currentHierarchy = new CategoryHierarchy(currentElement);

		while (iterator.hasNext()) {
			currentElement = iterator.next();
			LinkedHashSet<CategoryHierarchy> children = new LinkedHashSet(Set.of(currentHierarchy));
			currentHierarchy = new CategoryHierarchy(currentElement, children);
		}
		
		response.getCategorias().put(currentHierarchy.getRoot().getName(), currentHierarchy);
	}

	/**
	 * If vendible has a category, it's pushed to the node in response. Given that
	 * each Vendible is meant to have a meaningful category, the less abstract as
	 * it's possible, the method will also look for the vendible category parents
	 * and add them, so they're presented to the frontend to enhance the search.
	 */
	public static void addCategoriasToResponse(CategorizableObject vendible, CategorizableVendiblesResponse response) {
		Optional.ofNullable(vendible.getCategory()).ifPresent(category -> {
			List<VendibleCategoryDTO> toAddCategories = fetchHierachyForCategory(category);

			VendibleCategoryDTO rootCategory = toAddCategories.get(toAddCategories.size() - 1);
			boolean isMainCategoryInTree = response.getCategorias().containsKey(rootCategory.getName());

			if (!isMainCategoryInTree) {
				createAndInsertCategoryHierachy(response, toAddCategories);
			} else {
				// If the main category of the Vendible to be inserted exists, it means that a new CategoryHierachy has to be inserted under it.
				// This walks through all the category array. While the category exists (the current in the array is a child of the current in the tree),
				// The algorithm processes the next element at both structures. Once it finds a category in the array that isn't a child of current node in the tree,
				// it is inserted as one.
				Collections.reverse(toAddCategories);
				int i = 0;
				CategoryHierarchy currentHierarchy = response.getCategorias().get(toAddCategories.get(0).getName());

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

					Optional<CategoryHierarchy> nextToProcess = !arrayNextElementExists || currentHierarchy == null
							? Optional.empty()
							: currentHierarchy.getChildren().stream().filter(h -> h.getRoot().equals(nextArrayCategory))
									.findFirst();

					canContinueOverTree = nextToProcess.isPresent();

					if (canContinueOverTree) {
						currentHierarchy = nextToProcess.get();
					} else {
						if (nextArrayCategory != null && currentHierarchy != null) {
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

}
