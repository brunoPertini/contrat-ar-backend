package com.contractar.microserviciocommons.vendibles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.VendibleAccesor;
import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;
import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableObject;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableVendiblesResponse;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.VendibleCategoryDTO;

public final class VendibleHelper {
	private static VendibleCategoryDTO nextArrayCategory;
	private static CategoryHierarchy currentHierarchy;

	/**
	 * 
	 * @param category
	 * @return the category hierachy as a List, starting from itself to the highest
	 *         one, i.e., that without parent
	 */
	public static List<VendibleCategoryDTO> fetchHierachyForCategory(VendibleCategoryAccesor category) {
		List<VendibleCategoryDTO> toAddCategories = new ArrayList<VendibleCategoryDTO>();

		Optional<VendibleCategoryAccesor> parentOpt = Optional.ofNullable(category.getParent());
		Long parentId = parentOpt.isPresent() ? parentOpt.get().getId() : null;
		VendibleCategoryDTO dto = new VendibleCategoryDTO(category.getId(), category.getName(), parentId);
		toAddCategories.add(dto);

		while (parentOpt.isPresent()) {
			VendibleCategoryAccesor parent = parentOpt.get();
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
	 * 
	 * @param response
	 * @param toAddCategories
	 */
	private static void createAndInsertCategoryHierachy(CategorizableVendiblesResponse response,
			List<VendibleCategoryDTO> toAddCategories) {
		Iterator<VendibleCategoryDTO> iterator = toAddCategories.iterator();

		VendibleCategoryDTO currentElement = iterator.next();
		currentHierarchy = new CategoryHierarchy(currentElement);

		while (iterator.hasNext()) {
			currentElement = iterator.next();
			LinkedHashSet<CategoryHierarchy> children = new LinkedHashSet(Set.of(currentHierarchy));
			currentHierarchy = new CategoryHierarchy(currentElement, children);
		}
		
		// Despite the fact that in this flow the category is not in the response, given
		// presentational purposes it should be inserted as a new hierarchy under the key with
		// the same category name. 
		
		String rootCategoryName = currentHierarchy.getRoot().getName();
		
		Optional.ofNullable(response.getCategorias().get(rootCategoryName)).ifPresentOrElse(hierachiesList -> {
			hierachiesList.add(currentHierarchy);
		}, () -> {
			response.getCategorias().put(rootCategoryName, 
					new ArrayList<CategoryHierarchy>(Arrays.asList(currentHierarchy)));
		});
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

			boolean isMainCategoryInTree = response.getCategorias().values().stream().anyMatch(c -> c.stream()
					.anyMatch(innerCategory -> Objects.equals(innerCategory.getRootId(), rootCategory.getId())));

			if (!isMainCategoryInTree) {
				createAndInsertCategoryHierachy(response, toAddCategories);
			} else {
				// If the main category of the Vendible to be inserted exists, it means that a
				// new CategoryHierachy has to be inserted under it.
				// This walks through all the category array. While the category exists (the
				// current in the array is a child of the current in the tree),
				// The algorithm processes the next element at both structures. Once it finds a
				// category in the array that isn't a child of current node in the tree,
				// it is inserted as one.
				Collections.reverse(toAddCategories);
				int i = 0;
				
				VendibleCategoryDTO toBeInsertedCategory = toAddCategories.get(0);
							
				
				List<CategoryHierarchy> matchedHierachies = response.getCategorias()
						.values()
						.stream()
						.filter(hierachiesList -> hierachiesList
								.stream()
								.anyMatch(hierachy -> Objects.equals(hierachy.getRootId(), toBeInsertedCategory.getId()))
								)
						.findFirst()
						.get();
				
				CategoryHierarchy currentHierarchy = matchedHierachies
						.stream()
						.filter(h -> Objects.equals(h.getRootId(), toBeInsertedCategory.getId()))
						.findFirst()
						.get();
						
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
			VendibleAccesor vendible) {
		return vendible.getProveedoresVendibles().stream().map(proveedorVendible -> {
			ProveedorAccessor proveedor = proveedorVendible.getProveedor();
					proveedorVendible.getImagenUrl(), proveedorVendible.getStock(), proveedor.getId());
				proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(), proveedorVendible.getTipoPrecio(),
				proveedorVendible.getOffersDelivery(), proveedorVendible.getOffersInCustomAddress(), proveedorVendible.getImagenUrl(),
				proveedorVendible.getStock(), proveedorVendible.getCategory().getId());
			response.getProveedores().add(new ProveedorDTO(proveedor));

			return proveedorVendibleDTO;
		}).collect(Collectors.toSet());
	}

}
