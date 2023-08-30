package com.contractar.microserviciousuario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciousuario.models.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
    @SuppressWarnings("unchecked")
	public Usuario save(Usuario usuario);
    
    public Optional<Usuario> findById(Long id);
    
    public boolean existsById(Long id);

    public Usuario findByEmail(String email);
    
}
