package com.contractar.microserviciousuario.admin.dtos;

import java.util.ArrayList;
import java.util.List;

public final class ChangeRequestSearchDTO {
	private List<Long> searchIds;

	private List<String> searchAttributes;

	public ChangeRequestSearchDTO() {
		this.searchIds = new ArrayList<>();
		this.searchAttributes = new ArrayList<>();
	}

	public ChangeRequestSearchDTO(List<Long> searchIds, List<String> searchAttributes) {
		this.searchIds = searchIds;
		this.searchAttributes = searchAttributes;
	}

	public List<Long> getSearchIds() {
		return searchIds;
	}

	public void setSearchIds(List<Long> searchIds) {
		this.searchIds = searchIds;
	}

	public List<String> getSearchAttributes() {
		return searchAttributes;
	}

	public void setSearchAttributes(List<String> searchAttributes) {
		this.searchAttributes = searchAttributes;
	}

}
