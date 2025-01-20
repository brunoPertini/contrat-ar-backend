package com.contractar.microserviciousuario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciousuario.models.Suscripcion;

@Repository
public interface SuscripcionRepository extends CrudRepository<Suscripcion, Long> {
	@SuppressWarnings("unchecked")
	public Suscripcion save(Suscripcion s);
	
	public Optional<Suscripcion> findById(Long id);
	
	boolean existsByUsuario_Id(Long usuarioId);
}
