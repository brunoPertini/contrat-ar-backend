package com.contractar.microserviciocommons.dto.vendibles.category;

import java.util.LinkedHashSet;
import java.util.Set;

public class CategoryHierarchy {

	private VendibleCategoryDTO root;
	private Set<CategoryHierarchy> children;

	public CategoryHierarchy(VendibleCategoryDTO root, Set<CategoryHierarchy> children) {
		this.root = root;
		this.children = children;
	}

	public CategoryHierarchy() {
		this.root = null;
		this.children = new LinkedHashSet<CategoryHierarchy>();
	}

	public CategoryHierarchy(VendibleCategoryDTO root) {
		this.root = root;
		this.children = new LinkedHashSet<CategoryHierarchy>();
	}

	public VendibleCategoryDTO getRoot() {
		return root;
	}

	public void setRoot(VendibleCategoryDTO root) {
		this.root = root;
	}

	public Set<CategoryHierarchy> getChildren() {
		return children;
	}

	public void setChildren(Set<CategoryHierarchy> children) {
		this.children = children;
	}

	public static class CategoryHierachyDTO {
		private String root;
		private Set<CategoryHierachyDTO> children;

		public CategoryHierachyDTO(String root, Set<CategoryHierachyDTO> children) {
			this.root = root;
			this.children = children;
		}

		public CategoryHierachyDTO(String root) {
			this.root = root;
			this.children = new LinkedHashSet<CategoryHierachyDTO>();
		}

		public String getRoot() {
			return root;
		}

		public void setRoot(String root) {
			this.root = root;
		}

		public Set<CategoryHierachyDTO> getChildren() {
			return children;
		}

		public void setChildren(Set<CategoryHierachyDTO> children) {
			this.children = children;
		}
	}

}
