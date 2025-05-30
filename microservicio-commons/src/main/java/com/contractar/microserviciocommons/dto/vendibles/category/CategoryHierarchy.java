package com.contractar.microserviciocommons.dto.vendibles.category;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"root", "rootId", "children"})
/**
 * 
 *   This class organizes categories following this criteria: 
 * - Each hierarchy is made of a root element and a set of children, which are also hierarchies.
 * - The root of the hierarchy is that node which category's parent doesn't exist.
 * - The nodes without children are the "leaf nodes" of the entire hierarchy. 
 */
public class CategoryHierarchy {

	@JsonIgnore
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
	
	@JsonProperty("root")
	public String getRootName() {
		return root.getName();
	}
	
	@JsonProperty("rootId")
	public Long getRootId() {
		return root.getId();
	}

}
