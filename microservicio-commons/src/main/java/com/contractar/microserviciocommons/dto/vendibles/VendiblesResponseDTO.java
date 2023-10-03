package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.contractar.microserviciocommons.dto.ProveedorDTO;

public class VendiblesResponseDTO implements Comparator<VendibleCategoryDTO> {
	private Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles;
	private Set<ProveedorDTO> proveedores;
	private Set<VendibleCategoryDTO> categorias;

	private VendibleCategoryDTO currentCategory;
	private Map<Long, Boolean> parentsMap;

	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashMap<String, Set<SimplifiedProveedorVendibleDTO>>();
		this.proveedores = new LinkedHashSet<ProveedorDTO>();
		this.categorias = new LinkedHashSet<VendibleCategoryDTO>();
		this.currentCategory = new VendibleCategoryDTO();
		this.parentsMap = new HashMap<Long, Boolean>();
	}

	public VendiblesResponseDTO(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles,
			Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
		this.categorias = new LinkedHashSet<VendibleCategoryDTO>();
		this.currentCategory = new VendibleCategoryDTO();
		this.parentsMap = new HashMap<Long, Boolean>();
	}

	public Map<String, Set<SimplifiedProveedorVendibleDTO>> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles) {
		this.vendibles = vendibles;
	}

	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}

	public Set<VendibleCategoryDTO> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<VendibleCategoryDTO> categorias) {
		this.categorias = categorias;
	}

	@Override
	public int compare(VendibleCategoryDTO c1, VendibleCategoryDTO c2) {
		if (c1.getName() != currentCategory.getName()) {
			currentCategory = c1;
			boolean isParentNode = this.getCategorias().stream().anyMatch(c -> {
				Optional<Long> parentIdOpt = Optional.ofNullable(c.getParentId());
				if (parentIdOpt.isPresent()) {
					return c.getParentId().equals(currentCategory.getId());
				}

				return false;
			});
			this.parentsMap.put(currentCategory.getId(), isParentNode);
		}

		boolean someHasNullParentId = c1.getParentId() == null || c2.getParentId() == null;

		if (!someHasNullParentId || parentsMap.get(currentCategory.getId())) {
			if (c1.getId().equals(c2.getParentId())) {
				return -1;
			} else if (c2.getId().equals(c1.getParentId())) {
				return 1;
			} else {
				return 1;
			}
		} else {
			boolean bothHaveNullParentId = c1.getParentId() == null && c2.getParentId() == null;

			if (bothHaveNullParentId) {
				return 1;
			} else {
				if (c1.getParentId() == null && c2.getParentId() != null) {
					return -1;
				} else {
					return 1;
				}
			}

		}
	}
}
