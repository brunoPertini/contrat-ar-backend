package com.contractar.microserviciousuario.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contractar.microserviciousuario.models.Cliente;

public interface ClienteAdminRepository extends JpaRepository<Cliente, Long> {
	public List<Cliente> findAll();
	
	List<Cliente> findAllByNameContainingIgnoreCase(String name);
	
	List<Cliente> findAllBySurnameContainingIgnoreCase(String surname);
	
	List<Cliente> findAllByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);
}
