package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;


/**
 * Info that requires ADMIN approval to be inserted or modified as it is sensible
 */
public class UsuarioSensibleInfoDTO {

	private String email;
	private String password;
	
	public UsuarioSensibleInfoDTO() {}

	public UsuarioSensibleInfoDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
