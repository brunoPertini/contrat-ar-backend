package com.contractar.microserviciocommons.dto.vendibles.category;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;

public class CategoryHierarchy implements Comparator<VendibleCategoryDTO> {

	private TreeSet<VendibleCategoryDTO> categoriesTree;

	private VendibleCategoryDTO currentCategory = new VendibleCategoryDTO();
	private Map<Long, Boolean> parentsMap = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> childrenMap = new HashMap<Long, Boolean>();

	public CategoryHierarchy() {
		this.categoriesTree = new TreeSet<VendibleCategoryDTO>(this);
		this.currentCategory = new VendibleCategoryDTO();
		this.parentsMap = new HashMap<Long, Boolean>();
		this.childrenMap = new HashMap<Long, Boolean>();
	}
	
	private void setCurrentCategoryData(VendibleCategoryDTO category, Optional<Long> currentCategoryParentOpt) {
		if (category.getName() != currentCategory.getName()) {
			currentCategory = category;
			boolean isParentNode = categoriesTree.stream().anyMatch(c -> {
				Optional<Long> parentIdOpt = Optional.ofNullable(c.getParentId());
				if (parentIdOpt.isPresent()) {
					return c.getParentId().equals(currentCategory.getId());
				}

				return false;
			});
			
			boolean isChildNode = currentCategoryParentOpt.isPresent() &&
				categoriesTree
				.stream()
				.anyMatch(c -> {
					return c.getId().equals(currentCategoryParentOpt.get());
				});
			
			this.parentsMap.put(currentCategory.getId(), isParentNode);
			this.childrenMap.put(currentCategory.getId(), isChildNode);
		}
	}
	
	public Optional<VendibleCategoryDTO> getRoot() {
		try {
			return Optional.of(categoriesTree.first());
		} catch(NoSuchElementException e) {
			return Optional.empty();
		}
	}
	
	public TreeSet<VendibleCategoryDTO> getCategoriesTree() {
		return categoriesTree;
	}

	public void setCategoriesTree(TreeSet<VendibleCategoryDTO> categoriesTree) {
		this.categoriesTree = categoriesTree;
	}
	
	public void addCategory(VendibleCategoryDTO category) {
		categoriesTree.add(category);
	}
	
	public void addAllCategories(List<VendibleCategoryDTO> categories) {
		categoriesTree.addAll(categories);
	}

	@Override
	public int compare(VendibleCategoryDTO c1, VendibleCategoryDTO c2) {
		if (c1.getName().equals(c2.getName())) {
			return 0;
		}
		
		// Is the root node
		if (c1.getParentId() == null) {
			return -1;
		}
		
		Optional<Long> currentCategoryParentOpt = Optional.ofNullable(c1.getParentId());
		
		this.setCurrentCategoryData(c1, currentCategoryParentOpt);
		
		Long parentId = currentCategoryParentOpt.get();

		// Im child node
		if (this.childrenMap.get(currentCategory.getId()) && c2.getId().equals(parentId)) {			
			return 1;
		}
		
		// Im parent node
		if (this.parentsMap.get(currentCategory.getId())) {
			Optional<Long> categoryParentOpt = Optional.ofNullable(c2.getParentId());
			boolean imCategoryParent = categoryParentOpt.isPresent() && categoryParentOpt.get().equals(currentCategory.getId());
			if (imCategoryParent) {
				return -1;
			}
			
			return 1;
		}
		
		return 1;
	}

}
