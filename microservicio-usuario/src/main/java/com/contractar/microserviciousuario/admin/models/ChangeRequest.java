package com.contractar.microserviciousuario.admin.models;

import java.io.Serializable;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Represents a change that is requested by a Cliente or Proveedor, but has to be confirmed by an ADMIN. Implies an UPDATE 
 * operation on the database. 
 */
@Entity
public class ChangeRequest implements Serializable {

	private static final long serialVersionUID = -7844170990511109741L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String sourceTableIdName;
	
	@Column(nullable = false)
	private Long sourceTableId;

	@Column(nullable = false)
	private String sourceTable;
	
	@Column(nullable = false)
	private String attributes;

	private boolean wasApplied;
	
	public ChangeRequest() {}

	public ChangeRequest(String table, String attributes, boolean wasApplied, Long sourceTableId, String sourceTableIdName) {
		this.sourceTable = table;
		this.attributes = attributes;
		this.wasApplied = wasApplied;
		this.sourceTableId = sourceTableId;
		this.sourceTableIdName = sourceTableIdName;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public void setTable(String table) {
		this.sourceTable = table;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	public boolean wasApplied() {
		return wasApplied;
	}

	public void setWasApplied(boolean wasApplied) {
		this.wasApplied = wasApplied;
	}
	
	public String getSourceTableIdName() {
		return sourceTableIdName;
	}

	public void setSourceTableIdName(String sourceTableIdName) {
		this.sourceTableIdName = sourceTableIdName;
	}
	
	public Long getSourceTableId() {
		return sourceTableId;
	}

	public void setSourceTableId(Long sourceTableId) {
		this.sourceTableId = sourceTableId;
	}
}
