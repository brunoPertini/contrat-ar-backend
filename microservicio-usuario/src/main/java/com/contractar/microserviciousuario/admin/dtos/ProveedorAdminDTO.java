package com.contractar.microserviciousuario.admin.dtos;

import java.time.LocalDate;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "surname", "email", "birthDate", "phone",
	"location", "createdAt", "active", "dni", "fotoPerfilUrl", "suscripcion" })
public class ProveedorAdminDTO extends ProveedorDTO {
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate createdAt;
	
	public ProveedorAdminDTO(ProveedorAccessor p) {
		super(p);
		this.createdAt = p.getCreatedAt();
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
}
