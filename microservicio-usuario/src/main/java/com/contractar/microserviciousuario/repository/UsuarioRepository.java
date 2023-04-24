package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciousuario.models.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>{
    public Usuario save(Usuario usuario);

    public Usuario findByEmail(String email);

}
