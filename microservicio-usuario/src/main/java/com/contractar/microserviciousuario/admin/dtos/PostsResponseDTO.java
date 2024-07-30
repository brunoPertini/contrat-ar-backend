package com.contractar.microserviciousuario.admin.dtos;

import org.springframework.data.domain.Page;

import com.contractar.microserviciocommons.dto.vendibles.SliderDTO;

public class PostsResponseDTO extends SliderDTO{
	private Page<ProveedorVendibleAdminDTO> content;
	
	public PostsResponseDTO(Page<ProveedorVendibleAdminDTO> content) {
		this.content = content;
	}

	public Page<ProveedorVendibleAdminDTO> getContent() {
		return content;
	}

	public void setContent(Page<ProveedorVendibleAdminDTO> content) {
		this.content = content;
	}
	
	
}
