package com.contractar.microserviciousuario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	public Optional<Role> findByNombre(String nombre);
}
