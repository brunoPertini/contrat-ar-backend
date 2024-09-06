package com.contractar.microserviciousuario.admin.models;

import java.io.Serializable;
import java.util.List;

import com.contractar.microserviciousuario.models.converters.LongListConverter;
import com.contractar.microserviciousuario.models.converters.StringListConverter;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
	
	@Convert(converter = StringListConverter.class)
	@Column(nullable = false)
	private List<String> sourceTableIdNames;
	
	@Convert(converter = LongListConverter.class)
	@Column(nullable = false)
	private List<Long> sourceTableIds;

	@Column(nullable = false)
	private String sourceTable;
	
	@Column(nullable = false)
	private String attributes;

	private boolean wasApplied;
	
	public ChangeRequest() {}

	public ChangeRequest(String table, String attributes, boolean wasApplied, List<Long> sourceTableIds, List<String> sourceTableIdNames) {
		this.sourceTable = table;
		this.attributes = attributes;
		this.wasApplied = wasApplied;
		this.sourceTableIds = sourceTableIds;
		this.sourceTableIdNames = sourceTableIdNames;
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
	
	public List<String> getSourceTableIdNames() {
		return sourceTableIdNames;
	}

	public void setSourceTableIdNames(List<String> sourceTableIdNames) {
		this.sourceTableIdNames = sourceTableIdNames;
	}
	
	public List<Long> getSourceTableIds() {
		return sourceTableIds;
	}

	public void setSourceTableId(List<Long> sourceTableIds) {
		this.sourceTableIds = sourceTableIds;
	}
}
