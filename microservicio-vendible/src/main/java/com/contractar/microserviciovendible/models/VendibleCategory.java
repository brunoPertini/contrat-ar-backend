package com.contractar.microserviciovendible.models;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class VendibleCategory implements Comparable<VendibleCategory>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 200)
	private String name;
	
	@OneToOne(optional = true)
	private VendibleCategory parent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VendibleCategory getParent() {
		return parent;
	}

	public void setParent(VendibleCategory parent) {
		this.parent = parent;
	}
	
	public VendibleCategory() {}

	public VendibleCategory(Long id, String name, VendibleCategory parent) {
		this.id = id;
		this.name = name;
		this.parent = parent;
	}

	public VendibleCategory(String name, VendibleCategory parent) {
		this.name = name;
		this.parent = parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		VendibleCategory category = (VendibleCategory) obj;
		if (this.getId() != null) {
			return this.getId() == category.getId();
		}
		return this.getName() != category.getName();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getName());
	}

	/**
	 * If the current category is c's parent, then its lower than c.
	 */
	@Override
	public int compareTo(VendibleCategory c) {
		if (this.getId() == c.getId()) {
			return 0;
		}
		
		if (this.getId() == c.getParent().getId()) {
			return -1;
		}
		
		return 1;
	}
}
