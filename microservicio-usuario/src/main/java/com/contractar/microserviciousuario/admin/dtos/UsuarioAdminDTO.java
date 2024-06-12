package com.contractar.microserviciousuario.admin.dtos;

import java.time.LocalDate;

import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "surname", "email", "birthDate", "phone",
	"location", "createdAt", "isActive"  })
public class UsuarioAdminDTO extends UsuarioDTO {
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate createdAt;
	
	public UsuarioAdminDTO(UsuarioAccesor u) {
		super(u);
		this.createdAt = u.getCreatedAt();
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
}
