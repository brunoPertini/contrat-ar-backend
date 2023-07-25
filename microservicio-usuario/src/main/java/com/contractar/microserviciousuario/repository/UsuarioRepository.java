package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciousuario.models.Usuario;

@Repository
public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>{
    public Usuario save(Usuario usuario);
    
    public Usuario findById(Long id);

    public Usuario findByEmail(String email);

}
