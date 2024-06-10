package com.contractar.microserviciousuario.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contractar.microserviciousuario.models.Proveedor;

public interface ProveedorAdminRepository extends JpaRepository<Proveedor, Long> {
	List<Proveedor> findAllByNameContainingIgnoreCase(String name);
	
	List<Proveedor> findAllBySurnameContainingIgnoreCase(String surname);
	
	List<Proveedor> findAllByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);
	
	public List<Proveedor> findAll();

}
