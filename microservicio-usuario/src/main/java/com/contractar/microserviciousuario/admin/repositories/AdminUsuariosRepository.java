package com.contractar.microserviciousuario.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;

public interface AdminUsuariosRepository extends PagingAndSortingRepository<Usuario, Long> {
	@Query("SELECT p FROM Proveedor p")
	public List<Proveedor> getAllProveedores();
	
	@Query("SELECT c FROM Cliente c")
	public List<Cliente> getAllClientes();
}
