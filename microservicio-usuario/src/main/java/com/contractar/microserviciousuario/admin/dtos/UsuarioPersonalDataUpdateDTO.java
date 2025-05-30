package com.contractar.microserviciousuario.admin.dtos;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class UsuarioPersonalDataUpdateDTO extends UsuarioSensibleInfoDTO{
	private String name;
	private String surname;
	private LocalDate birthDate;
	private String phone;

	@JsonSerialize(using = UbicacionSerializer.class)
	@JsonDeserialize(using = UbicacionDeserializer.class)
	private Point location;


	public UsuarioPersonalDataUpdateDTO() {
	}

	public UsuarioPersonalDataUpdateDTO(String name, String surname, LocalDate birthDate, String phone, Point location,
			Boolean active) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.phone = phone;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
	
	@Override
	public String getChangeDetailUrl(Long userId) {
		return UsersControllerUrls.GET_USUARIO_INFO.replace("{userId}", userId.toString());
	}
}
